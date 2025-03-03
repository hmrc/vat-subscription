# vat-subscription

[![Build Status](https://travis-ci.org/hmrc/vat-subscription.svg)](https://travis-ci.org/hmrc/vat-subscription) [ ![Download](https://api.bintray.com/packages/hmrc/releases/vat-subscription/images/download.svg) ](https://bintray.com/hmrc/releases/vat-subscription/_latestVersion)

## Summary
This is the backend service which retrieves customer information for all VATVC services.

## Running

In order to run this microservice, you must have SBT installed. You should be able to start the application using:

`./run.sh`

## Endpoints

### GET /vat-subscription/:vatNumber/mandation-status

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
   "mandationStatus": "MTDfB Exempt"
}
```
Where:
* **mandationStatus** can be "MTDfB Exempt", "MTDfB", "Non MTDfB" or "Non Digital"

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
    "effectiveRegistrationDate": "2018-01-01",
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
        "organisationName": "Example Business Name",
        "tradingName": "Example Trading Name",
        "effectiveRegistrationDate": "2018-01-01",
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
        "organisationDetails": true,
        "PPOBDetails": true,
        "bankDetails": true,
        "returnPeriod": true,
        "deregister": false,
        "annualAccounting": false
    },
    "pendingChanges": {
        "PPOBDetails": {
            "address": {
                "line1": "New Add Line 1",
                "line2": "New Add Line 2",
                "line3": "New Add Line 3",
                "line4": "New Add Line 4",
                "line5": "New Add Line 5",
                "postCode": "NE33WER",
                "countryCode": "GB"
            },
            "contactDetails": {
                "primaryPhoneNumber": "112345678902",
                "mobileNumber": "112345678903",
                "faxNumber": "112345678904",
                "emailAddress": "testuser234@test.com",
                "emailVerified": true
            },
            "websiteAddress": "www.site.biz"
        },
        "bankDetails": {
            "accountHolderName": "Monty Mole",
            "bankAccountNumber": "****9999",
            "sortCode": "99****"
        },
        "returnPeriod": {
            "stdReturnPeriod": "MM"
        },
        "mandationStatus": "MTDfB Exempt",
        "commsPreference": "PAPER",
        "tradingName": "New Trading Name",
        "organisationName": "New Business Name"
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
* **pendingChanges** is optional and consists of optional elements "ppob", "bankDetails", "returnPeriod", "mandationStatus", "commsPreference", "tradingName" and "organisationName".
* **partyType** is optional.
* **primaryMainCode** is optional.
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

* **stdReturnPeriod** is mandatory and can take values "MA", "MB", "MC" or "MM", "YA", "YB", "YC", "YD", "YE", "YF", "YG", "YH", "YI", "YJ", "YK", "YL".


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
        "mobileNumber": "07712345679",
        "faxNumber": "07712345680",
        "emailAddress": "test@testemail.com",
        "emailVerified": "true"
    },
    "websiteAddress": "www.internet.com",
    "transactorOrCapacitorEmail": "testAgent@testemail.com"
}
```
Where:

* **address** is mandatory and consists of mandatory fields "line1" and "line2" and optional "line3", "line4", "postCode" and "nonUkCountryCode" fields.
* **contactDetails** is optional and consists of all optional fields; "phoneNumber", "mobileNumber", "faxNumber", "emailAddress" and "emailVerified".
* **websiteAddress** is optional.
* **transactorOrCapacitorEmail** is optional.

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
### PUT /vat-subscription/:vatNumber/email-address

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
        "mobileNumber": "07712345679",
        "faxNumber": "07712345680",
        "emailAddress": "test@testemail.com",
        "emailVerified": "false"
    },
    "websiteAddress": "www.internet.com"
}
```
Where:

* **address** is mandatory and consists of mandatory fields "line1" and "line2" and optional "line3", "line4", "postCode" and "nonUkCountryCode" fields.
* **contactDetails** is optional and consists of all optional fields; "phoneNumber", "mobileNumber", "faxNumber", "emailAddress" and "emailVerified".
* **websiteAddress** is optional.

### PUT /vat-subscription/:vatNumber/contact-preference

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
   "commsPreference": "DIGITAL"
}
```
Where:
* **commsPreference** can be "PAPER" or "DIGITAL"

### PUT /vat-subscription/:vatNumber/contact-preference/email

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
   "emailAddress": "test@test.email.com"
}
```
Where:
* **emailAddress** can be a new valid email address

### PUT /vat-subscription/:vatNumber/mandation-status

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
   "mandationStatus": "3",
   "transactorOrCapacitorEmail: = "testAgent@test.com"
}
```
Where:
* **mandationStatus** can be "1", "2", "3" or "4"
* **transactorOrCapacitorEmail** is optional.

### PUT /vat-subscription/:vatNumber/trading-name

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
   "transactorOrCapacitorEmail: = "testAgent@test.com",
   "tradingName": "MyTradingName"
}

```
Where:
* **transactorOrCapacitorEmail** is optional.
* **tradingName** is optional.

### PUT /vat-subscription/:vatNumber/business-name

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

The request takes a body of the form:
```
{
   "transactorOrCapacitorEmail: = "testAgent@test.com",
   "organisationName": "MyOrgName"
}

```
Where:
* **transactorOrCapacitorEmail** is optional.
* **organisationName** is optional.

### GET /vat-subscription/:vatNumber/standing-request

This API specification describes the data structures used in the requests and responses relating to standing requests.
It allows the consumer of the API to view the standing requests in ETMP.
The consumer of this API will be able to view the information by providing regime, tax payer ID type and tax payer ID value. 
The API will then respond with the corresponding details of standing requests or an appropriate error message.

Where:

* **:vatNumber** is a valid VRN, for example: "999999999"

#### Success Response

**HTTP Status**: 200

**Example HTTP Response Body**:
```
{
  "processingDate": "2024-07-15T09:30:47Z",
  "standingRequests": [
    {
      "requestNumber": "20000037272",
      "requestCategory": "3",
      "createdOn": "2023-11-30",
      "changedOn": "2024-12-26",
      "requestItems": [
        {
          "period": "1",
          "periodKey": "24A1",
          "startDate": "2024-02-01",
          "endDate": "2024-04-30",
          "dueDate": "2024-03-31",
          "amount": 22945.23,
          "chargeReference": "XD006411191344",
          "postingDueDate": "2024-03-31"
        },
        {
          "period": "2",
          "periodKey": "24A1",
          "startDate": "2024-02-01",
          "endDate": "2024-04-30",
          "dueDate": "2024-04-30",
          "amount": 22945.23,
          "chargeReference": "XD006411191345",
          "postingDueDate": "2024-04-30"
        },
        {
          "period": "3",
          "periodKey": "24A2",
          "startDate": "2024-05-01",
          "endDate": "2024-07-31",
          "dueDate": "2024-06-30",
          "amount": 22945.23,
          "chargeReference": "XD006411191346",
          "postingDueDate": "2024-06-30"
        },
        {
          "period": "4",
          "periodKey": "24A2",
          "startDate": "2024-05-01",
          "endDate": "2024-07-31",
          "dueDate": "2024-07-31",
          "amount": 22945.23,
          "chargeReference": "XD006411191347",
          "postingDueDate": "2024-07-31"
        },
        {
          "period": "5",
          "periodKey": "24A3",
          "startDate": "2024-08-01",
          "endDate": "2024-10-31",
          "dueDate": "2024-09-30",
          "amount": 22945.23,
          "chargeReference": "XD006411191348",
          "postingDueDate": "2024-09-30"
        },
        {
          "period": "6",
          "periodKey": "24A3",
          "startDate": "2024-08-01",
          "endDate": "2024-10-31",
          "dueDate": "2024-10-31",
          "amount": 22945.23,
          "chargeReference": "XD006411191349",
          "postingDueDate": "2024-10-31"
        },
        {
          "period": "7",
          "periodKey": "24A4",
          "startDate": "2024-11-01",
          "endDate": "2025-01-31",
          "dueDate": "2024-12-31",
          "amount": 22945.23,
          "chargeReference": "XD006411191350",
          "postingDueDate": "2024-12-31"
        },
        {	"period": "8",
          "periodKey": "24A4",
          "startDate": "2024-11-01",
          "endDate": "2025-01-31",
          "dueDate": "2025-01-31",
          "amount": 22945.23,
          "chargeReference": "XD006411191351",
          "postingDueDate": "2025-01-31"
        }

      ]
    },
    {
      "requestNumber": "20000037277",
      "requestCategory": "3",
      "createdOn": "2024-11-30",
      "changedOn": "2025-01-26",
      "requestItems": [
        {
          "period": "1",
          "periodKey": "25A1",
          "startDate": "2025-02-01",
          "endDate": "2025-04-30",
          "dueDate": "2025-03-31",
          "amount": 122945.23
        },
        {
          "period": "2",
          "periodKey": "25A1",
          "startDate": "2025-02-01",
          "endDate": "2025-04-30",
          "dueDate": "2025-04-30",
          "amount": 122945.23
        },
        {
          "period": "3",
          "periodKey": "25A2",
          "startDate": "2025-05-01",
          "endDate": "2025-07-31",
          "dueDate": "2025-06-30",
          "amount": 122945.23
        },
        {
          "period": "4",
          "periodKey": "25A2",
          "startDate": "2025-05-01",
          "endDate": "2025-07-31",
          "dueDate": "2025-07-31",
          "amount": 122945.23
        },
        {
          "period": "5",
          "periodKey": "25A3",
          "startDate": "2025-08-01",
          "endDate": "2025-10-31",
          "dueDate": "2025-09-30",
          "amount": 122945.23
        },
        {
          "period": "6",
          "periodKey": "25A3",
          "startDate": "2025-08-01",
          "endDate": "2025-10-31",
          "dueDate": "2025-10-31",
          "amount": 122945.23
        },
        {
          "period": "7",
          "periodKey": "25A4",
          "startDate": "2025-11-01",
          "endDate": "2026-01-31",
          "dueDate": "2025-12-31",
          "amount": 122945.23
        },
        {
          "period": "8",
          "periodKey": "25A4",
          "startDate": "2025-11-01",
          "endDate": "2026-01-31",
          "dueDate": "2026-01-31",
          "amount": 122945.23
        }

      ]
    }
  ]
}
```
Where:
* **processingDate** is processing date.
* **standingRequests** is Standing Requests.
* **requestNumber** is Request Number.
* **requestCategory** Request Category, possible values: 3 - VAT POA Instalment.
* **createdOn** Created On.
* **changedOn** Changed On.
* **requestItems** Request Items contained within the Request Number.
* **period** is Period.
* **periodKey** is Period Key.
* **startDate** is Start Date.
* **endDate** is End Date.
* **dueDate** is Due Date.
* **amount** Amount (11 Digits before Decimal and 2 after decimal).
* **chargeReference** is Charge Reference.
* **postingDueDate** Posting Due Date.

#### Error Responses

##### INVALID_VAT_NUMBER
* **Status**: 400

##### VAT_NUMBER_NOT_FOUND
* **Status**: 404


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").


## Testing
`sbt clean coverage test it/test coverageReport`