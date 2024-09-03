# Кредитный конвейер

Авторы задания [@alxdv97](https://github.com/alxdv97) и [@MichaelDeyev](https://github.com/MichaelDeyev)

## Архитектура

![Архитектура](/images/architechture.drawio.png)

## Задание  
   Разработать приложение "Кредитный конвейер" с применением технологий и инструментов:

- Java (Kotlin)
- SpringBoot (Web, JPA, Redis, Cloud, Security, AOP)
- OpenAPI
- Kafka
- JUnit, Mockito

##  Conventions

Во всех микросервисах используется данный стек технологий:

### Общее:
- Стек: Java 11 & Lombok (Kotlin 1.6) + SpringBoot v2.7;
- Тестирование: Junit4/5, Mockito. Целевой процент покрытия - 80%;
- СУБД: Postgres + liquibase (проперти spring.jpa.hibernate.ddl-auto: validate);
- Для Java: API & DTO генерировать через OpenAPI maven generator, маппинг сущностей с использованием фреймворка mapstruct
- Документация API через swagger;
- Все REST-endpoints должны возвращать ResponseEntity<DTO>; Request/response логгирование от каждого МСа;
- Синхронное взаимодействие: Feign + FeignErrorDecoder (для проброса ошибок между МСами);
- Асинхронное взаимодействие: Kafka;

### Git:
- Добавить проект в github, отвести от ветки main ветку develop. Рекомендуемая структура папок: <папка_с_полным_именем_проекта>/<папка_с_МС>
- Все дальнейшие изменения производить из веток, отведенных от develop через pull request в develop.
- Имена этих веток формируются следующим образом: <feature/bugfix>/<MS-name>/<1-3_слова_о_изменениях>
- Использовать только безопасные операции: `commit`, `push`, `pull`, `fetch`, `merge`, `checkout`

## План выполнения:

### MVP Level 1.
Реализация МС "Кредитный Конвейер", основная задача которого - 
расчет всевозможных параметров кредита на основе полученных данных, проведение [скоринга](#scoring).

#### API:
`POST: /conveyor/offers` - расчёт возможных условий кредита. 

- Request - [LoanApplicationRequestDTO](#LoanApplicationRequestDTO)
- Response - List<[LoanOfferDTO](#LoanOfferDTO)>

По API приходит [LoanApplicationRequestDTO](#LoanApplicationRequestDTO).

На основании [LoanApplicationRequestDTO](#LoanApplicationRequestDTO) происходит [прескоринг](#prescoring), создаётся 4 кредитных предложения [LoanOfferDTO](#LoanOfferDTO) на основании всех возможных комбинаций булевских полей 
isInsuranceEnabled и isSalaryClient (false-false, false-true, true-false, true-true). Логику формирования кредитных предложений можно придумать самому. 
К примеру: в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита, базовая ставка хардкодится в коде через property файл. 
Например, цена страховки 100к (или прогрессивная, в зависимости от запрошенной суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3. 
Цена зарплатного клиента 0, уменьшает ставку на 1.

Ответ на API - список из 4х [LoanOfferDTO](#LoanOfferDTO) от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше).

`POST: /conveyor/calculation` - валидация присланных данных + [скоринг](#scoring) данных + полный расчет параметров кредита. 
- Request - [ScoringDataDTO](#ScoringDataDTO)
- Response [CreditDTO](#CreditDTO).
По API приходит [ScoringDataDTO](#ScoringDataDTO).
Происходит [скоринг](#scoring) данных, высчитывание ставки(`rate`), полной стоимости кредита(`psk`), размер ежемесячного платежа(`monthlyPayment`), график ежемесячных платежей (List<[PaymentScheduleElement](#PaymentScheduleElement)>). 
Логику расчета параметров кредита можно найти в интернете (пример), полученный результат сверять с имеющимися в интернете калькуляторами графиков платежей и ПСК.

Ответ на API - [CreditDTO](#CreditDTO), насыщенный всеми рассчитанными параметрами.

### MVP Level 2.

Реализация микросервиса Сделка (deal), основная задача которого - реализовывать весь бизнес процесс и хранить состояние системы

#### Определить сущности для БД

_Прим.: Для упрощения допускается поля employment, passport, appliedOffer, statusHistory хранить в формате jsonb._
_Так же допускается связь o2o для отношения application-client и application-credit._

Структура БД
![Entities](/images/entities.png)

### Легенда:

#### Цвета:
- **Зеленый**: сущность реализована в виде отдельного отношения
- **Желтый**: сущность реализована в виде поля типа `jsonb`
- **Синий**: сущность реализована в виде `enum`, сохранена в бд как `varchar`

#### Реализовать логику работы API:
`POST: /deal/application`

- Request - [LoanApplicationRequestDTO](#LoanApplicationRequestDTO)
- Response - List<[LoanOfferDTO](#LoanOfferDTO)>

По API приходит [LoanApplicationRequestDTO](#LoanApplicationRequestDTO)
На основе [LoanApplicationRequestDTO](#LoanApplicationRequestDTO) создаётся сущность `Client` и сохраняется в БД.
Создаётся `Application` со связью на только что созданный `Client` и сохраняется в БД.
Отправляется POST запрос на /conveyor/offers МС conveyor через `FeignClient`. 
Каждому элементу из списка List<[LoanOfferDTO](#LoanOfferDTO)> присваивается id созданной заявки (Application)

Ответ на API - список из 4-х [LoanOfferDTO](#LoanOfferDTO) от "худшего" к "лучшему".

`PUT: /deal/offer`

- Request [LoanOfferDTO](#LoanOfferDTO)
- Response `void`

По API приходит [LoanOfferDTO](#LoanOfferDTO).
Достаётся из БД заявка(`Application`) по `applicationId` из [LoanOfferDTO](#LoanOfferDTO).
В заявке обновляется статус, история статусов(List<[ApplicationStatusHistoryDTO](#ApplicationStatusHistoryDTO)>), 
принятое предложение [LoanOfferDTO](#LoanOfferDTO) устанавливается в поле `appliedOffer`.

Заявка сохраняется.

`PUT: /deal/calculate/{applicationId}`

- Request [FinishRegistrationRequestDTO](#FinishRegistrationRequestDTO), param - `Long`
- Response `void`

По API приходит объект [FinishRegistrationRequestDTO](#FinishRegistrationRequestDTO) и параметр `applicationId`.

Достаётся из БД заявка(`Application`) по `applicationId`.
[ScoringDataDTO](#ScoringDataDTO) насыщается информацией из [FinishRegistrationRequestDTO](#FinishRegistrationRequestDTO) и `Client`, который хранится в `Application`.
Отправляется POST запрос на `/conveyor/calculation` МС conveyor с телом [ScoringDataDTO](#ScoringDataDTO) через `FeignClient`.
На основе полученного из кредитного конвейера [CreditDTO](#CreditDTO) создаётся сущность `Credit` и сохраняется 
в базу со статусом `CALCULATED`.

В заявке обновляется статус, история статусов.
Заявка сохраняется.

### MVP Level 3. Перенос прескоринга в МС-application
Реализация микросервиса Заявка (application)


#### Реализовать логику работы API МС-application:
`POST: /application`
- Request [LoanApplicationRequestDTO](#LoanApplicationRequestDTO)
- Response List<[LoanOfferDTO](#LoanOfferDTO)>

По API приходит [LoanApplicationRequestDTO](#LoanApplicationRequestDTO).

На основе [LoanApplicationRequestDTO](#LoanApplicationRequestDTO) происходит [прескоринг](#prescoring).
Отправляется POST-запрос на `/deal/application` в МС deal через `FeignClient`.
Ответ на API - список из 4-х [LoanOfferDTO](#LoanOfferDTO) от "худшего" к "лучшему".

`POST: /application/offer`
- Request [LoanOfferDTO](#LoanOfferDTO)
- Response `void`

По API приходит [LoanOfferDTO](#LoanOfferDTO)

Отправляется POST-запрос на `/deal/offer` в МС deal через `FeignClient`.

### MVP Level 4. Настройка уведомлений о ходе сделки.
Реализация микросервиса Кредитное Досье (dossier), основная задача которого - на основании полученный из kafka 
сообщений отправлять email-оповещения на почту клиенту и формировать документы о кредите (кредитное досье)

#### Формирование письма и документов. 

1. Необходимо создать несколько кредитных документов, например: Кредитный Договор, Анкета, График Платежей. 
2. Наполнение документов можно придумать самостоятельно, но достаточно будет создать .txt файлы и наполнить их данными. 
В кредитный договор обычно помещаются все имеющиеся данные, в анкету - только присланные на 1-2 запросе и в 
график кладется рассчитанный график платежей.
3. Отправка письма на почту Клиенту. Почтовый сервис можно выбрать самостоятельно, например, Яндекс. 
4. Нужно будет создать аккаунт, создать пароль приложения и в качестве данных для подключения в application.yaml 
указывать его. Не забудьте разрешить отправку через IMAP!
_Также иногда почтовые сервисы не хотят отправлять сообщения, потому что принимают их за спам. 
С этим сложно что-то поделать, иногда через день ограничение снимается._

#### Добавление API в МС-deal
- `POST: /deal/document/{applicationId}/send` - запрос на отправку документов.
- `POST: /deal/document/{applicationId}/sign` - запрос на подписание документов
- `POST: /deal/document/{applicationId}/code` - подписание документов

Первая отправка письма на почту клиенту должна происходить в самом конце существующей API `PUT: /deal/offer`

В запросе от МС-deal к МС-dossier используется ДТО [EmailMessage](#EmailMessage)

#### Реализовать взаимодействие через Kafka между МС-deal и МС-dossier

МС-deal выступает в роли издателя (producer), МС-dossier в роли подписчика (consumer)
Завести в кафке 6 топиков, соответствующие темам, по которым необходимо направить письмо на почту Клиенту:
- `finish-registration` - событие отправляется после выбора конкретного кредитного предложения. 
На почту отправляется email, предлагающий пользователю окончить регистрацию.
- `create-documents` - событие отправляется после успешного прохождения [скоринга](#scoring). На почту клиенту отправляется 
предложение отправить запрос на создание документов.
- `send-documents` - событие отправляется после запроса на создание документов. На почту клиенту отправляются 
сформированные документы и отправить запрос на подписание документов.
- `send-ses` - событие отправляется после запроса на подписание документов. На почту клиенту отправляется код 
ПЭП (SES) и предложение отправить этот код вместе с id заявки по ссылке подписание.
- `credit-issued` - событие отправляется после подписания документов. На почту клиенту отправляется сообщение 
с подтверждением успешного взятия кредита.
- `application-denied` - событие отправляется либо если пользователь сам отказался от кредита, либо провалил 
этап [скоринга](#scoring). На почту клиенту отправляется сообщение о том, что заявка отклонена.


### MVP Level 5. Реализация паттерна API-Gateway
Реализация микросервиса gateway который реализует паттерн API-Gateway для сервисов кредитного конвейера.

Суть паттерна заключается в том, что клиент отправляется запросы не в несколько разных МС с бизнес-логикой, 
а только в один МС (gateway), чтобы он уже перенаправлял запросы в другие МСы. 

Главная задача этого МС - инкапсулировать сложную логику всей внутренней системы, 
предоставив клиенту простой и понятный API. Достаточно реализовать через создание микросервиса, который 
не делает ничего, кроме маршрутизации запросов от пользователя в другие МСы и передача ответа от МСов пользователю.

#### В МС-deal добавить админские API
- `GET: /deal/admin/application/{applicationId} `- получить заявку по id
- `GET: /deal/admin/application` - получить все заявки

### MVP Level 6. DevOPs

#### Контейнеризация проекта.
Поместить все проекты в docker-compose контейнер, настроить их взаимодействие.

#### Настроить CI-пайплайн в git.
Настроить CI на сборку, вычисление процента покрытия тестами (см. CodeCov) и проверки качества кода (см. SonarCloud).

### MVP Level 7. Мониторинг

#### Реализовать мониторинг технического состояния приложений и некоторых бизнес-значений с использованием Prometheus/Grafana.
1. Развернуть в docker-compose Prometheus и Grafana.
2. Добавить зависимость на SpringBoot Actuator в МС Deal
3. Подключить spring-приложение к Prometheus зависимость io.micrometer:micrometer-registry-prometheus
4. Добавить в Actuator эндпоинт Prometheus'а
5. Создать в resources файл prometheus.yaml, описать в нем настройки Prometheus'а, 
параметры подключения Prometheus'а к приложению
6. Подключить Grafana к Prometheus для визуализации метрик. Можно использовать готовые дашборды Grafana, 
например, 6756 или 4701
7. Также реализовать отправление кастомных метрик по изменениям статусов заявки. 

Можно использовать интерфейс `Counter` для подсчета заявок в текущем статусе: 
после инициализации приложения создаются `Counter` для каждого статуса, и далее, при обновлении статуса вызывается 
инкремент `Counter'а`.

Пример инициализации Counter'ов
```java
public class MonitoringTest {
    @PostConstruct
    private void initStatusCounters(){
        Arrays.stream(ApplicationStatus.values())
                .forEach(status -> Counter.builder(COUNTER_STATUS_NAME)
                        .description("Number of applications in each status")
                        .tag("status", status.name())
                        .register(meterRegistry)
                );
    }
}
```
Принцип работы:

1. При запуске приложения `Spring Actuator` выставляет эндпоинты для `Prometheus`
2. Сервер `Prometheus` слушает их, забирает с них всю информацию
3. Сервер `Grafana` подключается к серверу `Prometheus` и визуализирует все данные.

### MVP Level 8. Аутентификация с использованием JWT
Создать МС `Auth`, ответственный за аутентификацию пользователей.

МС Auth будет выполнять следующие обязанности:

- Сохранение данных о пользователе (логин/пароль) при регистрации. Пароль требуется кодировать в Base64.
- Проверка введенных данных пользователя при аутентификации
- Формирование и выдача `JWT`. Время жизни выбирается произвольно, рекомендуется ~10 минут
- Проверка корректности `JWT` при каждом запросе к МС Gateway. 
При обращении к другим сервисам использовать JWT не требуется, так как считается, что остальные МС 
находятся внутри контура, в который пропускаются только авторизированные запросы.

Также требуется добавить в МС `Gateway` эндпоинты `registration` и `login`, которые будут принимать логин и 
пароль пользователя и перенаправлять запрос в МС Auth для регистрации и аутентификации соответственно.

Логика работы:

1. Пользователь регистрируется в систему, его логин и пароль отправляются на хранение через МС `Gateway` в МС `Auth`
2. Пользователь логинится в систему, запрос идет из МС `Gateway` в МС `Auth`, где проверяется на соответствие логин и пароль
3. В ответ МС `Auth` возвращает сущность, состоящую из логина и JWT пользователя.
4. Пользователь при каждом следующем запросе к МС `Gateway` кладет присланный JWT в заголовок авторизации 
(тип авторизации Bearer Token)
5. МС Gateway при каждом пришедшем от пользователя запросе отправляет присланный JWT на проверку в МС `Auth`.
6. Если проверка прошла, запрос идет дальше по МСам
7. Если проверка не прошла, пользователю возвращается 403 ошибка

### MVP Level 9. Добавление аудита

- Создать МС `audit`, в который будут отправляться события для аудита асинхронно, по kafka.
- Создать `kafka` топик `audit-action`
- Создать МС `audit`, подключить его к `kafka`
- Создать хранилище NoSQL `Redis` в докер-контейнере, подключить МС `audit` к нему. Для взаимодействия МС Audit 
и БД использовать Spring Data Redis
- В МС создать сущность `AuditAction (uuid: UUID, type: Enum[START,SUCCESS,FAILURE]service: 
Enum[APPLICATION,DEAL,CONVEYOR,DOSSIER], message: String)`
- При поступлении события аудита сохранять его в `Redis`
- Создать REST-endpoints (только для User с ролью ADMIN) c реализацией полнотекстового поиска 
(specification api) для взятия события по uuid, по type, по service и получения списка всех событий. 
- Прокинуть эндпоинты в МС `Gateway`.

Логика интеграции с МС `Audit`:

Отправлять из МС `Deal` события аудита.

В поле message полностью описать текущее событие - время, id заявки и прочая важная информация для текущего контекста

События должны отправляться вначале необходимого метода, при успешном завершении, и при ошибочном завершении метода

Необходимые методы для покрытия аудитом (описание согласно схеме в п. Business-flow):
- Расчет возможных условий кредита
- Заявка сохраняется со статусом APPROVED
- Валидация присланных данных и расчет всех параметров кредита
- Заявка обновляется со статусом DOCUMENT_SIGNED
- Выдача кредита (статус заявки CREDIT_ISSUED)
- Так же для выполнения норм безопасности требуется выполнять событие аудита для методов взятия заявки по id и взятия всех заявок.

### MVP Level 10.Тестирование с помощью testcontainers
Необходимо написать несколько интеграционных тестов для МС Deal. БД развернуть с помощью `testcontainers`

### MVP Level 11. Интеграция с ELK
Необходимо интегрировать стек ELK (Elasticsearch, Logstash, Kibana) с микросервисами кредитного конвейера.

Данная технология используется для централизованной сборки и хранения логов, 
а также удобного поиска по ним. 
- `Elasticsearch` - механизм полнотекстового поиска, 
- `Logstash` - инструмент сборки логов, 
- `Kibana` - UI для визуализации логов.

##  Логика работы

1. Пользователь отправляет заявку на кредит.
2. МС Заявка осуществляет [прескоринг](#prescoring) заявки и если [прескоринг](#prescoring) проходит, то заявка 
сохраняется в МС Сделка и отправляется в КК.
3. КК возвращает через МС `Application` пользователю 4 предложения `LoanOffer` по кредиту с разными условиями 
(например без страховки, со страховкой, с зарплатным клиентом, со страховкой и зарплатным клиентом) или отказ.
4. Пользователь выбирает одно из предложений, отправляется запрос в МС `Application`, а оттуда в МС `Deal`, 
где заявка на кредит и сам кредит сохраняются в базу.
5. МС `Dossier` отправляет клиенту письмо с текстом "Ваша заявка предварительно одобрена, завершите оформление".
6. Клиент отправляет запрос в МС `Deal` со всеми своими полными данными о работодателе и прописке. Происходит
[скоринг](#scoring) данных в МС `CC`, `CC` рассчитывает все данные по кредиту (ПСК, график платежей и тд), 
МС `Deal` сохраняет обновленную заявку и сущность кредит сделанную на основе [CreditDTO](#CreditDTO) полученного 
из `CC` со статусом CALCULATED в БД.
7. После валидации МС `Dossier` отправляет письмо на почту клиенту с одобрением или отказом. Если кредит одобрен, то в 
письме присутствует ссылка на запрос "Сформировать документы"
8. Клиент отправляет запрос на формирование документов в МС `Dossier`, МС `Dossier` отправляет клиенту на почту документы для 
подписания и ссылку на запрос на согласие с условиями.
9. Клиент может отказаться от условий или согласиться. Если согласился - МС `Dossier` на почту отправляет код и ссылку 
на подписание документов, куда клиент должен отправить полученный код в МС `Deal`.
10. Если полученный код совпадает с отправленным, МС `Deal` выдает кредит (меняет статус сущности `Credit` на `ISSUED`, 
а статус заявки на `CREDIT_ISSUED`)


### Правила скоринга:

#### Prescoring

Правила прескоринга:
- Имя, Фамилия - от 2 до 30 латинских букв. Отчество, при наличии - от 2 до 30 латинских букв.
- Сумма кредита - действительно число, большее или равное 10000.
- Срок кредита - целое число, большее или равное 6.
- Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня.
- Email адрес - строка, подходящая под паттерн [\w\.]{2,50}@[\w\.]{2,20}
- Серия паспорта - 4 цифры, номер паспорта - 6 цифр.

#### Scoring

Правила скоринга:
- Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 3
- Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 4
- Сумма займа больше, чем 20 зарплат → отказ
- Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
- Количество иждивенцев больше 1 → ставка увеличивается на 1
- Возраст менее 20 или более 60 лет → отказ
- Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; Не бинарный → ставка увеличивается на 3
- Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ

## Business-flow
![business-flow](/images/business-flow.png)

### Легенда:

#### Цвета:
- Оранжевый: МС Заявка
- Голубой: МС Сделка + БД
- Фиолетовый: МС Кредитный Конвейер
- Зеленый: МС Кредитное Досье
- Красный: конец флоу

#### Типы действий:
- Иконка "человек" сверху слева: пользовательское действие
- Иконка "зубчатое колесо" сверху слева: действие системы
- Иконка "молния": ошибка
- Иконка "прямоугольник с горизонтальными полосками": выбор пользователя
- Иконка "конверт": асинхронная отправка email-сообщения на почту

## Sequence-диаграмма

![sequence diagram](/images/sequence-diagram.vpd.png)

## Модели API
### LoanApplicationRequestDTO
```json
{
    "amount": "BigDecimal",
    "term": "Integer",
    "firstName": "String",
    "lastName": "String",
    "middleName": "String",
    "email": "String",
    "birthdate": "LocalDate",
    "passportSeries": "String",
    "passportNumber": "String"
}
```
### LoanOfferDTO
```json
{
    "applicationId": "Long",
    "requestedAmount": "BigDecimal",
    "totalAmount": "BigDecimal",
    "term": "Integer",
    "monthlyPayment": "BigDecimal",
    "rate": "BigDecimal",
    "isInsuranceEnabled": "Boolean",
    "isSalaryClient": "Boolean"
}
```
### ScoringDataDTO
```json
{
  "amount": "BigDecimal",
  "term": "Integer",
  "firstName": "String",
  "lastName": "String",
  "middleName": "String",
  "gender": "Enum",
  "birthdate": "LocalDate",
  "passportSeries": "String",
  "passportNumber": "String",
  "passportIssueDate": "LocalDate",
  "passportIssueBranch": "String",
  "maritalStatus": "Enum",
  "dependentAmount": "Integer",
  "employment": "EmploymentDTO",
  "account": "String",
  "isInsuranceEnabled": "Boolean",
  "isSalaryClient": "Boolean"
}
```
### CreditDTO
```json
{
    "amount": "BigDecimal",
    "term": "Integer",
    "monthlyPayment": "BigDecimal",
    "rate": "BigDecimal",
    "psk": "BigDecimal",
    "isInsuranceEnabled": "Boolean",
    "isSalaryClient": "Boolean",
    "paymentSchedule": "List<PaymentScheduleElement>"
}
```
### FinishRegistrationRequestDTO
```json
{
    "gender": "Enum",
    "maritalStatus": "Enum",
    "dependentAmount": "Integer",
    "passportIssueDate": "LocalDate",
    "passportIssueBranch": "String",
    "employment": "EmploymentDTO",
    "account": "String"
}
```
### EmploymentDTO
```json
{
    "employmentStatus": "Enum",
    "employerINN": "String",
    "salary": "BigDecimal",
    "position": "Enum",
    "workExperienceTotal": "Integer",
    "workExperienceCurrent": "Integer"
}
```
### PaymentScheduleElement
```json
{
    "number": "Integer",
    "date": "LocalDate",
    "totalPayment": "BigDecimal",
    "interestPayment": "BigDecimal",
    "debtPayment": "BigDecimal",
    "remainingDebt": "BigDecimal"
}
```
### ApplicationStatusHistoryDTO
```json
{
    "status": "Enum",
    "time": "LocalDateTime",
    "changeType": "Enum"
}
```
### EmailMessage
```json
{
    "address": "String",
    "theme": "Enum",
    "applicationId": "Long"
}
```
