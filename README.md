# PackageService

PackageService is a very basic microservice written to take products from [Product Service](https://product-service.herokuapp.com/api/v1) and allow packages to be created from those products. It also supports conversion to a couple of different currencies, you can see which currencies it supports on [Fixer](http://fixer.io/).

PackageService is built with scalability in mind, requests to the currency and product services are ran regularly separate from requests. This means load to our downstream services is the exact same even when our TPS increases. New products are pulled from the Product Service every 10 seconds, the new exchange rate is pulled from Fixer every day at 4:15pm CET — Fixer states on their website "rates are updated daily around 4PM CET," the 'around' bit isn't too good for us but we'll assume their rate pulling is done by 4:15pm.

| Method | Endpoint                  | Description                              | Request Format                           |
| ------ | ------------------------- | ---------------------------------------- | ---------------------------------------- |
| GET    | /package                  | List all packages.                       | `{"name": "My Test Package.", "description": "My great package", "products": ["VqKb4tyj9V6i"]}` |
| POST   | /package                  | Create a new package                     |                                          |
| GET    | /package/:id?currency=GBP | Get a package with an optional currency. |                                          |
| PUT    | /package/:id              | Update a package. All parameters are optional for this API call. Note: if you pass `products` the original products that were in this package will be replaced with the new products passed. | `{"name": "My Test Package.", "description": "My great package", "products": ["VqKb4tyj9V6i"]}` |
| DELETE | /package/:id              | Delete a package.                        |                                          |

There is currently no persistent storage, `PackageRepository` should be refactored to use DynamoDB and then the current functionality of the class can be repurposed as a cache in front of DynamoDB. There are also no unit or e2e tests written.