# vat-subscription

[![Build Status](https://travis-ci.org/hmrc/vat-subscription.svg)](https://travis-ci.org/hmrc/vat-subscription) [ ![Download](https://api.bintray.com/packages/hmrc/releases/vat-subscription/images/download.svg) ](https://bintray.com/hmrc/releases/vat-subscription/_latestVersion)

## Summary
This is the backend for `Manage-vat-subscription-frontend` service.

## Running
`sbt "run 9567 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

## Feature Switches

Get Current State of Switches:
`curl -X GET http://localhost:9567/vat-subscription/test-only/feature-switch`

Change Feature Switches:
`curl -X POST http://localhost:9567/vat-subscription/test-only/feature-switch -H 'content-type: application/json' -d '{ "latestApi1363Version": false, "latestApi1365Version": false, "stubDes" : false }'`

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
   "hasFlatRateScheme": true,
   "isPartialMigration": false
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
        "hasFlatRateScheme": true,
        "isPartialMigration": false
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
        "bankAccountNumber": "****0001",
        "sortCode": "****56"
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
* **ppob** is mandatory and consists of mandatory "line1" and "countryCode" with optional "line2", "line3", "line4", "line5" and "postCode".
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

### GET /vat-subscription/:vatNumber/manage-account-summary

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

Provides a summary view of the Customers VAT Subscription for the Manage Account section on the Business Tax Account.

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{   
    "mandationStatus": "MTDfB Mandated",
    "ppobAddress": {
      "line1": "addLine1",
      "line2": "addLine2",
      "line3": "addLine3",
      "line4": "addLine4",
      "line5": "addLine5",
      "postCode": "TE3 3TT,
      "countryCode": "EN"
    },
    "contactEmail": "test@testemail.com",
    "businessName": "Grimey Org",
    "repaymentBankDetails": {
       "accountHolderName": "Monty Burns",
       "bankAccountNumber": "****0001",
       "sortCode": "****56"
    }
}
```
Where:
* **mandationStatus** is mandatory and can be "MTDfB Mandated", "MTDfB Voluntary", "Non MTDfB" or "Non Digital".
* **ppobAddress** is mandatory and consists of mandatory "line1" and "countryCode" with optional "line2", "line3", "line4", "line5" and "postCode".
* **bankDetails** is optional and its elements are also all optional.
* **businessName** is optional and is only returned if there is an organisation name for the VRN

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

The request takes a body of the form:
```
{
    "stdReturnPeriod": MA"
}
```
Where:

* **stdReturnPeriod** is mandatory and can take values "MA", "MB", "MC" or "MM".


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


### PUT /vat-subscription/:vatNumber/deregister

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
    "deregReason": "belowThreshold",
    "ceasedTradingDate": "2018-12-22",
    "deregLaterDate": "2018-12-30",
    "intendSellCapitalAssets": true,
    "capitalAssetsValue": 12.99,
    "optionToTax": true,
    "optionToTaxValue": 500.21,
    "stocksValue": 600.99,
    "cashAccountingScheme": true,
    "additionalTaxInvoices": true,
    "turnoverBelowThreshold": {
        "belowThreshold": "belowPast12Months",
        "nextTwelveMonthsTurnover": 650.55,
        "whyTurnoverBelow": {
            "closedPlacesOfBusiness": true,
            "lostContract": true,
            "moreCompetitors": true,
            "reducedTradingHours": true,
            "seasonalBusiness": true,
            "semiRetiring": true,
            "turnoverLowerThanExpected": true
        }
    }
}
```
Where:

* **deregReason** is mandatory and should be `ceased` or `belowThreshold`
* **deregDate** is mandatory IF the deregReason is `ceased`
* **deregLaterDate** is optional and only required if the Taxpayer wants a delayed date of deregistration
* **intendSellCapitalAssets** is mandatory and should be `true` or `false`
* **capitalAssetsValue** is mandatory IF intendSellCapitalAssets is `true`
* **optionToTax** is mandatory and should be `true` or `false`
* **optionToTaxValue** is mandatory IF optionToTax is `true`
* **stocksValue** is optional
* **cashAccountingScheme** is mandatory and should be `true` or `false`
* **additionalTaxInvoices** is mandatory, true if the Taxpayer has outstanding invoices to issue or be paid for
* **turnoverBelowThreshold** is mandatory IF the deregReason is `belowThreshold`
    * **belowThreshold** is mandatory and should be `belowPast12Months` or `belowNext12Months`
    * **nextTwelveMonthsTurnover** is mandatory
    * **whyTurnoverBelow** is mandatory IF the belowThreshold reason is `belowNext12Months`
        * **closedPlacesOfBusiness** is mandatory and should be `true` or `false`
        * **lostContract** is mandatory and should be `true` or `false`
        * **moreCompetitors** is mandatory and should be `true` or `false`
        * **reducedTradingHours** is mandatory and should be `true` or `false`
        * **seasonalBusiness** is mandatory and should be `true` or `false`
        * **semiRetiring** is mandatory and should be `true` or `false`
        * **turnoverLowerThanExpected** is mandatory and should be `true` or `false`


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
