# vat-subscription

[![Build Status](https://travis-ci.org/hmrc/vat-subscription.svg)](https://travis-ci.org/hmrc/vat-subscription) [ ![Download](https://api.bintray.com/packages/hmrc/releases/vat-subscription/images/download.svg) ](https://bintray.com/hmrc/releases/vat-subscription/_latestVersion)

## Summary
This is the backend service which retrieves customer information for all VATVC services.

## Running
`sbt "run 9567 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

or alternatively:

`./run.sh`

## Feature Switches

Get Current State of Switches:
`curl -X GET http://localhost:9567/vat-subscription/test-only/feature-switch`

Change Feature Switches:
`curl -X POST http://localhost:9567/vat-subscription/test-only/feature-switch -H 'content-type: application/json' -d '{ "latestApi1363Version": false, "latestApi1365Version": false}'`

[API1363Version & API1365Version]
The API version features are set to "Latest" when they are working correctly against the most recent DES spec.
If there is a need to implement upcoming functionality that would not work yet in Production, the old version can be referred to as "Pre-release".

["enableNewStatusIndicators"]
When on this will use the following mandation statuses:
"Exempt", "MTDfb", "Non MTDfb" or "Non Digital"

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
    "title": "0001",
    "firstName": "Frank",
    "middleName": "Andrew",
    "lastName": "Grimes",
    "vatRegistrationDate": "2018-01-01",
    "customerMigratedToETMPDate": "2018-01-01",
    "hybridToFullMigrationDate": "2018-01-01",
    "hasFlatRateScheme": true,
    "welshIndicator": false,
    "isPartialMigration": false,
    "isInsolvent": false,
    "insolvencyType": "03",
    "insolvencyDate": "2018-01-02",
    "continueToTrade": true,
    "overseasIndicator": false,
    "nameIsReadOnly": false
}
```

Where the fields "hasFlatRateScheme" and "overseasIndicator" are mandatory, and all other fields are optional.

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
        "title": "0001",
        "firstName": "Frank",
        "middleName": "Andrew",
        "lastName": "Grimes",
        "vatRegistrationDate": "2018-01-01",
        "customerMigratedToETMPDate": "2018-01-01",
        "hybridToFullMigrationDate": "2018-01-01",
        "hasFlatRateScheme": true,
        "welshIndicator": false,
        "isPartialMigration": false,
        "isInsolvent": false,
        "insolvencyType": "03",
        "insolvencyDate": "2018-01-02",
        "continueToTrade": true,
        "overseasIndicator": false,
        "nameIsReadOnly": false
    },
    "flatRateScheme": {
        "FRSCategory": "001",
        "FRSPercentage": 134,
        "limitedCostTrader": true,
        "startDate": "2001-01-01"
    },
    "ppob": {
        "address": {
            "line1": "VAT PPOB Line1",
            "line2": "VAT PPOB Line2",
            "line3": "VAT PPOB Line3",
            "line4": "VAT PPOB Line5",
            "postCode": "TF3 4ER",
            "countryCode": "EN"
        },
        "contactDetails": {
            "primaryPhoneNumber": "012345678901",
            "mobileNumber": "07814 557733",
            "faxNumber": "012345678903",
            "emailAddress": "person@earth.com",
            "emailVerified": true
        },
        "websiteAddress": "www.site.com"
    },
    "bankDetails": {
        "accountHolderName": "Monty Burns",
        "bankAccountNumber": "****0001",
        "sortCode": "****56"
    },
    "returnPeriod": {
        "stdReturnPeriod": "MA",
        "nonStdTaxPeriods": [
            {
                "periodStart": "2018-01-01",
                "periodEnd": "2018-01-15"
            },
            {
                "periodStart": "2018-01-16",
                "periodEnd": "2018-01-31"
            }
        ]
        "firstNonNSTPPeriod": {
            "periodStart": "2018-02-01",
            "periodEnd": "2018-04-30"
        }
    },
    "changeIndicators": {
        "organisationDetails": false,
        "PPOBDetails": false,
        "bankDetails": false,
        "returnPeriod": true,
        "deregister": false,
        "annualAccounting": false
    },
    "pendingChanges": {
        "returnPeriod": {
            "stdReturnPeriod": "MB"
        }
    },
    "partyType": "50",
    "primaryMainCode": "10410",
    "missingTrader": false,
    "commsPreference": "DIGITAL"
}
```
Where:
* **mandationStatus** can be "MTDfB Exempt", "MTDfB", "Non MTDfB" or "Non Digital".
* **customerDetails** is mandatory and consists of mandatory "hasFlatRateScheme" and "overseasIndicator", with all other fields in this block being optional.
* **flatRateScheme** is optional and its elements are also all optional.
* **ppob** is mandatory and consists of mandatory "line1" and "countryCode", with all other fields in this block being optional.
* **bankDetails** is optional and its elements are also all optional.
* **returnPeriod** is optional and its elements are also all optional.
* **changeIndicators** is optional and consists of mandatory boolean values to denote which sections of data have pending changes.
* **pendingChanges** is optional and consists of optional elements "ppob", "bankDetails", "returnPeriod", "mandationStatus", "commsPreference" and "tradingName".
* **partyType** is optional.
* **primaryMainCode** is mandatory.
* **missingTrader** is mandatory.
* **commsPreference** is optional.

#### Error Responses

##### INVALID_VAT_NUMBER
* **Status**: 400

##### VAT_NUMBER_NOT_FOUND
* **Status**: 404

##### UNEXPECTED_GET_VAT_CUSTOMER_INFORMATION_FAILURE
* **Status**: (any)

### GET /vat-subscription/:vatNumber/manage-account-summary

This endpoint is deprecated and `/vat-subscription/:vatNumber/full-information` should be used instead.

Endpoint removed January 2021

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
