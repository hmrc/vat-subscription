# vat-subscription

[![Build Status](https://travis-ci.org/hmrc/vat-subscription.svg)](https://travis-ci.org/hmrc/vat-subscription) [ ![Download](https://api.bintray.com/packages/hmrc/releases/vat-subscription/images/download.svg) ](https://bintray.com/hmrc/releases/vat-subscription/_latestVersion)

## Summary
This is the backend for `Manage-vat-subscription-frontend` service.

## Running
`sbt "run 9567 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

## Endpoints

### GET /vat-subscription/:vatNumber/mandation-status

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "mandationStatus": "MTDfB Mandated"
}
```
Where:
* **mandationStatus** can be "MTDfB Mandated", "MTDfB Voluntary", "Non MTDfB" or "Non Digital"

#### Error Responses

##### INVALID_VAT_NUMBER
* **Status**: 400

##### VAT_NUMBER_NOT_FOUND
* **Status**: 404

##### UNEXPECTED_GET_VAT_CUSTOMER_INFORMATION_FAILURE
* **Status**: (any)

### GET /vat-subscription/:vatNumber/customer-details

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "firstName": "Frank",
   "lastName": "Grimes",
   "organisationName": "Grimey Org",
   "tradingName": "Grimey Ltd",
   "hasFlatRateScheme": true
}
```

#### Error Responses

##### INVALID_VAT_NUMBER
* **Status**: 400

##### VAT_NUMBER_NOT_FOUND
* **Status**: 404

##### UNEXPECTED_GET_VAT_CUSTOMER_INFORMATION_FAILURE
* **Status**: (any)

### GET /vat-subscription/:vatNumber/full-information

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
    "mandationStatus": "MTDfB Mandated",
    "customerDetails": {
        "firstName": "Frank",
        "lastName": "Grimes",
        "organisationName": "Grimey Org",
        "tradingName": "Grimey Ltd",
        "hasFlatRateScheme": true
    },
    "flatRateScheme": {
        "FRSCategory": "001",
        "FRSPercentage": 134,
        "limitedCostTrader": true,
        "startDate": "2001-01-01"
    },
    "ppob": {
        "address": {
            "line1": "64 Zoo Lane",
            "countryCode": "EN"
        },
        "contactDetails": {
            "phoneNumber": "07712345678",
            "emailAddress": "person@earth.com"
        },
        "websiteAddress": "www.site.com"
    },
    "bankDetails": {
        "accountHolderName": "Monty Burns",
        "bankAccountNumber": "00000001",
        "sortCode": "12-34-56"
    },
    "returnPeriod": "MA",
    "pendingChanges": {
        "returnPeriod": "MB"
    }
}
```
Where:
* **mandationStatus** can be "MTDfB Mandated", "MTDfB Voluntary", "Non MTDfB" or "Non Digital".
* **CustomerDetails** is mandatory.
* **flatRateScheme** is optional and its elements are also all optional.
* **ppob** is optional and consists of mandatory "line1" and "countryCode" with optional "line2", "line3", "line4", "line5" and "postCode".
* **bankDetails** is optional and its elements are also all optional.
* **returnPeriod** is optional and can be either "MA", "MB", "MC" or "MM".
* **pendingChanges** is optional and consists of optional elements "ppob", "bankDetails" and "returnPeriod".

#### Error Responses

##### INVALID_VAT_NUMBER
* **Status**: 400

##### VAT_NUMBER_NOT_FOUND
* **Status**: 404

##### UNEXPECTED_GET_VAT_CUSTOMER_INFORMATION_FAILURE
* **Status**: (any)

### PUT /vat-subscription/:vatNumber/return-period

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of either "MA", "MB", "MC" or "MM".

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "formBundle": "12345"
}
```

### PUT /vat-subscription/:vatNumber/ppob

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
    "address": {
        "line1": "21 Jump Street",
        "line2": "JMP STRT"
    },
    "contactDetails": {
        "phoneNumber": "07712345678",
        "mobileNumber": "07712345679"
    },
    "websiteAddress": "www.internet.com"
}
```
Where:

* **address** is mandatory and consists of mandatory fields "line1" and "line2" and optional "line3", "line4", "postCode" and "nonUkCountryCode" fields.
* **contactDetails** is optional and consists of all optional fields; "phoneNumber", "mobileNumber", "faxNumber", "emailAddress" and "emailVerified".
* **websiteAddress** is optional.

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "formBundle": "12345"
}
```


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 

 
