# cto-coding-java-muserver

mu-server documentation: https://muserver.io/

## Coding exrercise: Product UI Microservice

You are writing a small microservice that provides UI-ready information about **Products** combined with **Inventory** data.

* The microservice exposes one HTTP endpoint:

  > GET /ui/products?ids=1,2,5 — returns product info for the requested product IDs in the **UI response format** (see below).

* The microservice must call **two existing upstream REST APIs**:
  > GET /api/products/{id} — returns product core info (id, name, description, price).

  > GET /api/inventory/{id} — returns inventory info (productId, warehouse, qty, lastUpdated).

* Your service must:

    * Call the two APIs (for multiple product IDs).

    * Merge and augment the data (examples below).

    * Return a combined JSON array to the UI.

    * Be robust: handle missing upstream data, transient failures (retry with backoff), and partial results (return partial results and an errors array).

    * Be testable and structured with clear separation: controller → service → clients.

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
      "name": "Beta Sock",
      "description": "Thermal sock",
      "price": 9.99,
      "inventory": null,
      "availability": "UNKNOWN",
      "priceWithTax": 11.99
    }
  ],
  "errors": [
    { "id": 2, "message": "Inventory not found" }
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
* Implementation must be `idiomatic` for the chosen stack (e.g Java or Kotlin)
* Provide **unit tests** covering:
    * successful merge
    * missing inventory
    * upstream failures