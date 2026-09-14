# RabbitMQ Patterns with Spring Boot

A Spring Boot project demonstrating different **RabbitMQ messaging patterns** through practical, real-world use cases.

The project covers **Point-to-Point, Publish/Subscribe, Direct Exchange, Topic Exchange, and Work Queue** patterns, along with message conversion, acknowledgments, retries, Dead Letter Exchanges (DLX), TTL, publisher confirms, and monitoring.

---

## 🚀 Project Overview

The project contains five different RabbitMQ messaging scenarios:

| Messaging Pattern     | Use Case                                   |
| --------------------- | ------------------------------------------ |
| **Point-to-Point**    | Order processing                           |
| **Publish/Subscribe** | Social media notifications and analytics   |
| **Direct Exchange**   | Log processing based on log level          |
| **Topic Exchange**    | IoT device and sensor management           |
| **Work Queue**        | Image processing and workload distribution |

The goal is to understand how RabbitMQ can be used for **asynchronous communication and event-driven architectures** in Spring Boot applications.

---

## 🛠️ Technologies

* **Java**
* **Spring Boot**
* **Spring AMQP**
* **RabbitMQ**
* **Jackson**
* **Spring Web**
* **Spring Boot Actuator**
* **Springdoc OpenAPI / Swagger**
* **SLF4J / Logback**
* **Maven**

---

## 📋 Requirements

Before running the application, make sure the following are installed:

* Java 17+
* Maven
* RabbitMQ Server
* Erlang/OTP

### Default RabbitMQ Configuration

| Property         | Value       |
| ---------------- | ----------- |
| Host             | `localhost` |
| AMQP Port        | `5672`      |
| Username         | `guest`     |
| Password         | `guest`     |
| Virtual Host     | `/`         |
| Application Port | `8085`      |

> The default `guest` RabbitMQ user is generally intended for local connections. For production environments, a dedicated RabbitMQ user with appropriate permissions should be created.

---

# 🏗️ Architecture

The application exposes REST APIs that publish messages to RabbitMQ.

```text
                    ┌──────────────────────┐
                    │   Spring Boot API    │
                    │      Port 8085       │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │    RabbitTemplate    │
                    │   JSON Conversion    │
                    └──────────┬───────────┘
                               │
          ┌────────────────────┼────────────────────┐
          ▼                    ▼                    ▼
   ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
   │    Queue    │      │   Fanout    │      │   Direct    │
   │ Point-to-   │      │   Exchange  │      │   Exchange  │
   │   Point     │      │             │      │             │
   └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
          │                    │                    │
          ▼                    ▼                    ▼
     Consumers          Multiple Queues        Log Queues

                         ┌─────────────┐
                         │    Topic    │
                         │   Exchange  │
                         └──────┬──────┘
                                │
                                ▼
                         Routing Key
                           Matching
```

---

# 1. 📦 Point-to-Point Pattern

The **Point-to-Point** pattern sends a message to a queue where one consumer processes each message.

In this project, the pattern is used for **order processing**.

### Message Flow

```text
Client
  │
  ▼
OrderController
  │
  ▼
OrderService
  │
  ▼
order.processing.queue
  │
  ▼
Payment Consumer
```

### Queue

```text
order.processing.queue
```

### Features

* Create orders
* Send orders to a processing queue
* Distribute messages between consumers
* Dead Letter Exchange support

### API

#### Create an Order

```http
POST /api/orders
Content-Type: application/json
```

Example request:

```json
{
  "customerId": "customer-001",
  "customerEmail": "customer@example.com",
  "items": [
    {
      "productId": "product-001",
      "productName": "Laptop",
      "quantity": 1,
      "price": 25000.00
    },
    {
      "productId": "product-002",
      "productName": "Mouse",
      "quantity": 2,
      "price": 750.00
    }
  ]
}
```

#### Create a Sample Order

```http
POST /api/orders/sample
```

---

# 2. 📢 Publish/Subscribe Pattern

The **Publish/Subscribe** pattern broadcasts a message to multiple subscribers.

This project uses a **Fanout Exchange** to simulate social media event processing.

A single social media post can be consumed by multiple services independently.

### Message Flow

```text
                    ┌──────────────────┐
                    │   Social Post    │
                    │    Publisher     │
                    └────────┬─────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │     Fanout Exchange  │
                  │ social.fanout.exchange│
                  └──────────┬───────────┘
                             │
             ┌───────────────┼───────────────┐
             ▼               ▼               ▼
      ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
      │   Timeline   │ │ Notification │ │   Analytics  │
      │     Queue    │ │     Queue    │ │     Queue    │
      └──────────────┘ └──────────────┘ └──────────────┘
```

### Exchange

```text
social.fanout.exchange
```

### Queues

```text
social.timeline.queue
social.notification.queue
social.analytics.queue
```

### Use Cases

* Update user timelines
* Send notifications
* Process analytics
* Broadcast events to multiple independent consumers

### API

#### Create a Social Post

```http
POST /api/social/posts
Content-Type: application/json
```

Example:

```json
{
  "userId": "user-001",
  "username": "omer",
  "content": "Learning RabbitMQ with Spring Boot!",
  "imageUrls": [
    "https://example.com/image.jpg"
  ],
  "hashtags": [
    "java",
    "springboot",
    "rabbitmq"
  ],
  "mentions": [
    "user-002"
  ]
}
```

#### Create a Sample Post

```http
POST /api/social/posts/sample
```

#### Create a Text Post

```http
POST /api/social/posts/text-sample
```

#### Get User Timeline

```http
GET /api/social/timeline/{userId}
```

#### Get Analytics Summary

```http
GET /api/social/analytics/summary
```

#### Get User Analytics

```http
GET /api/social/analytics/user/{userId}
```

---

# 3. 🔀 Direct Exchange Pattern

A **Direct Exchange** routes messages based on an exact routing key.

This project uses it for **log processing**.

### Routing Keys

| Routing Key | Queue               | Purpose            |
| ----------- | ------------------- | ------------------ |
| `error`     | `log.error.queue`   | Error logs         |
| `warning`   | `log.warning.queue` | Warning logs       |
| `info`      | `log.info.queue`    | Informational logs |

### Message Flow

```text
LogController
     │
     ▼
  LogService
     │
     ▼
log.direct.exchange
     │
     ├── error   ──► log.error.queue
     │
     ├── warning ─► log.warning.queue
     │
     └── info    ──► log.info.queue
```

### Exchange

```text
log.direct.exchange
```

### API

#### Send an Error Log

```http
POST /api/logs/error
Content-Type: application/json
```

```json
{
  "applicationName": "payment-service",
  "message": "Payment processing failed",
  "source": "PaymentService",
  "exception": "Insufficient balance"
}
```

#### Send a Warning Log

```http
POST /api/logs/warning
```

```json
{
  "applicationName": "order-service",
  "message": "Order processing is taking longer than expected",
  "source": "OrderService"
}
```

#### Send an Info Log

```http
POST /api/logs/info
```

```json
{
  "applicationName": "user-service",
  "message": "User registered successfully",
  "source": "UserController"
}
```

#### Send a Debug Log

```http
POST /api/logs/debug
```

#### Generate Sample Logs

```http
POST /api/logs/sample-logs
```

#### Get Log Analytics

```http
GET /api/logs/analytics/summary
```

#### Get Service Analytics

```http
GET /api/logs/analytics/service/{serviceName}
```

---

# 4. 🌐 Topic Exchange Pattern

A **Topic Exchange** routes messages based on routing-key patterns.

This project uses Topic Exchange for **IoT device management**.

### Example Routing Keys

```text
sensor.temperature.livingroom
sensor.motion.frontdoor
device.camera.battery
```

### Topic Wildcards

| Wildcard | Meaning                    |
| -------- | -------------------------- |
| `*`      | Matches exactly one word   |
| `#`      | Matches zero or more words |

### Bindings

| Queue                 | Binding Pattern        |
| --------------------- | ---------------------- |
| `iot.hvac.queue`      | `sensor.temperature.*` |
| `iot.security.queue`  | `sensor.motion.*`      |
| `iot.battery.queue`   | `device.*.battery`     |
| `iot.analytics.queue` | `sensor.#`             |

### Message Flow

```text
                     IoT Controller
                           │
                           ▼
                  ┌──────────────────┐
                  │  Topic Exchange  │
                  │ iot.topic.exchange│
                  └────────┬─────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
  sensor.temperature.*  sensor.motion.*  device.*.battery
          │                │                │
          ▼                ▼                ▼
      HVAC Queue      Security Queue    Battery Queue

                     sensor.#
                        │
                        ▼
                 Analytics Queue
```

### API

#### Send Temperature Data

```http
POST /api/iot/sensor/temperature
```

```json
{
  "deviceId": "temp-001",
  "location": "livingroom",
  "temperature": 22.5
}
```

Routing key:

```text
sensor.temperature.livingroom
```

#### Send Motion Data

```http
POST /api/iot/sensor/motion
```

```json
{
  "deviceId": "motion-001",
  "location": "frontdoor",
  "motionDetected": true
}
```

Routing key:

```text
sensor.motion.frontdoor
```

#### Send Battery Status

```http
POST /api/iot/device/battery
```

```json
{
  "deviceId": "camera-001",
  "deviceType": "camera",
  "location": "frontdoor",
  "batteryLevel": 85.0,
  "charging": false
}
```

Routing key:

```text
device.camera.battery
```

#### Generate Sample IoT Data

```http
POST /api/iot/sample-data
```

---

# 5. 🖼️ Work Queue Pattern

The **Work Queue** pattern distributes time-consuming tasks between multiple workers.

This project uses it for **image processing**.

### Message Flow

```text
                  Image Processing API
                           │
                           ▼
                image.processing.queue
                           │
             ┌─────────────┼─────────────┐
             ▼             ▼             ▼
         Worker 1      Worker 2      Worker 3
             │             │             │
             ▼             ▼             ▼
          RESIZE       THUMBNAIL     WATERMARK
```

### Queue

```text
image.processing.queue
```

The queue is configured with:

* Durable queue
* Dead Letter Exchange
* 5-minute message TTL

### API

#### Submit an Image Processing Task

```http
POST /api/images/process
Content-Type: application/json
```

```json
{
  "imageUrl": "https://example.com/image.jpg",
  "userId": "user-001",
  "operations": [
    "RESIZE",
    "THUMBNAIL",
    "WATERMARK"
  ]
}
```

#### Submit 10 Sample Tasks

```http
POST /api/images/batch-process
```

This endpoint creates 10 sample image-processing tasks to demonstrate workload distribution between workers.

---

# ☠️ Dead Letter Exchange

The project also demonstrates a **Dead Letter Exchange (DLX)** for handling messages that cannot be processed normally.

### Architecture

```text
order.processing.queue
          │
          │ Dead Letter
          ▼
     dlx.exchange
          │
          ▼
      dlx.queue
```

### DLX Configuration

| Component   | Value          |
| ----------- | -------------- |
| Exchange    | `dlx.exchange` |
| Queue       | `dlx.queue`    |
| Routing Key | `dlx`          |

DLX can be used to:

* Preserve failed messages
* Investigate processing failures
* Implement retry/reprocessing workflows
* Prevent problematic messages from blocking normal processing

> Configuring a DLX does not automatically mean that every failed message will be sent to it. The message must meet a dead-letter condition, such as being rejected without requeue or expiring due to TTL.

---

# 🔄 JSON Message Conversion

RabbitMQ messages are transported as byte arrays.

Since the application works with Java objects such as `Order`, `IoTMessage`, `SocialPost`, and `ImageProcessingTask`, the project uses `JacksonJsonMessageConverter`.

```java
@Bean
public MessageConverter messageConverter() {
    return new JacksonJsonMessageConverter();
}
```

### Producer

```text
Java Object
     ↓
   JSON
     ↓
   byte[]
     ↓
 RabbitMQ
```

### Consumer

```text
 RabbitMQ
     ↓
   byte[]
     ↓
   JSON
     ↓
 Java Object
```

The same message converter is configured for
