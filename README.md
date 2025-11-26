# Coding Exercise: Product UI Microservice

## Pre-requisites

* JDK 21+
* Maven

The service is using mu-server as the web framework, however this has been abstracted away so you can focus on the coding exercise itself.

### Reference
mu-server documentation: https://muserver.io/

## Getting Started

* Clone the repository:
  ```bash
  git clone https://github.com/sunil99/cto-coding-java-muserver.git
  cd cto-coding-java-muserver
  ```
* Run the build using:
  ```bash
  mvn clean install
  ```
* Run the tests using:
  ```bash
  mvn test
  ```
  
* Use the LocalApp class to start the microservice. The logs will provide the URLs to access the endpoints.
  > /test/java/LocalApp.java


## The Coding Test

You are writing a small microservice that provides UI-ready information about **Products** combined with **Inventory** data.

* The microservice exposes one HTTP endpoint:

  > GET /ui/products?ids=1,2,5 — returns product info for the requested product IDs in the **UI response format** (see below).

* The microservice must call **two existing upstream REST APIs** which have been mocked for you in the test folder (MockInventoryService and MockProductService):
  > GET /api/products/{id} — returns product core info (id, name, description, price).

  > GET /api/inventory/{id} — returns inventory info (productId, warehouse, qty, lastUpdated).

* Your service must:

    * Call the two APIs (for multiple product IDs).

    * Merge and augment the data (examples below).

    * Return a combined JSON array to the UI (in the REST API - see below).

### Upstream sample responses

**GET /api/products/1**

```json
{
  "id": 1,
  "name": "Alpha Shoe",
  "description": "Lightweight running shoe",
  "price": 79.99
}
```

**GET /api/inventory/1**

```json
{
  "productId": 1,
  "warehouse": "LON-1",
  "qty": 12,
  "lastUpdated": "2025-11-01T12:00:00Z"
}

```

### UI response format
The microservice should return data in the following format:

```json
{
  "results": [
    {
      "id": 1,
      "name": "Alpha Shoe",
      "description": "Lightweight running shoe",
      "price": 79.99,
      "inventory": {
        "warehouse": "LON-1",
        "qty": 12,
        "lastUpdated": "2025-11-01T12:00:00Z"
      },
      "availability": "IN_STOCK",
      "priceWithTax": 95.99
    },
    {
      "id": 2,
      "name": "Beta Sneaker",
      "description": "Comfortable casual sneaker",
      "price": 59.99,
      "inventory":   {
        "warehouse": "LON-1",
        "qty": 0,
        "lastUpdated": "2025-11-01T12:00:00Z"
      },
      "availability": "OUT_OF_STOCK",
      "priceWithTax": 71.99
    },
    {
      "id": 3,
      "name": "Gamma Boot",
      "description": "Durable hiking boot",
      "price": 120.00,
      "inventory": null,
      "availability": "UNKNOWN",
      "priceWithTax": 144.00
    }
  ],
  "errors": [
    { "id": 3, "message": "Inventory not found" }
  ]
}

```

### Data augmentation rules
* `priceWithTax` = `price + 20% tax`, round to 2 decimal places
* `availability`:
    * `IN_STOCK` if qty > 0
    * `OUT_OF_STOCK` if qty = 0
    * `UNKNOWN` if inventory data is missing
* If one **upstream fails** for a product, include the product with available data and put `null` for missing parts. Add an entry to the `errors` array with `id` and `message`.
* Provide **unit tests** covering:
    * successful merge
    * missing inventory
    * upstream failures