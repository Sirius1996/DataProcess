# DataProcess

## Introduction
This project implements the procedure that applies the ***k-anonymity*** to the given dataset to achieve a certain level of protection of sensitive and private data. The released dataset provides k-anonymity protection for each record.

## Definition: K-anonymity
K-anonymity is a model that is designed to give protection to the information when it comes to the scenario where a set of data that contains multiple personal or other sensitive information will be published. The protection goal is to guarantee that none of the records could be re-identified by combining the released dataset with external related collections. The released dataset is said to have k-anonymity if one record can't be distinguished from at least k-1 other records in the release set. 

The set of attributes that in combination can uniquely identify individuals is called quasi-identifier. Usually attributes that could be publicly available should be considered to constitute the quasi-identifier, and should be controlled to ensure anonymity.

In reality, it is usually hard to determine which attributes will appear externally, so the ***k-anonymity requirement*** is introduced as a constraint to the released data. The idea is that the released data is said to satisfy the k-anonymity requirement if and only if each sequence of values in released data appears at least ***k*** times.

## Processing of the data set 
The given data set contains following attributes:
> age | workclass | fnlwgt | education | educational-num | marital-status | occupation | relationship | race | gender | capital-gain | capital-loss | hours-per-week | native-country | income

Among these attributes, 'fnlwgt' seems to be the unique identifier of each record, therefore it shouldn't be published. 

Assuming that Quasi-identifier **[age, workclass, captial-gain, captial-loss, hours-per-week]**, because these attributes could be used by linking with external datasets to uniquely identify a record in this dataset.

The generalization method is applied to ***ages, captial-gain, captial-loss, hours-per-week, occupations*** attributes by replacing exact values with a broader category. The suppression method could also be applied to these attributes based on different requirements.
           
## Usage
Please set up the ARX library and related libraries. Compile the java file with following cmd:
```shell
javac -cp [ARX library path] DataProcess.java
```

In this project, the ARX library has been put under the path `resources/lib`, so you could compile with
```shell
javac -cp ../resources/lib/libarx.jar DataProcess.java
```

This program takes the path of raw data set as input, so run with
```shell
java -cp .:../resources/lib/libarx.jar DataProcess ../resources/data/adult.csv
```

The result is put in `resources/data/result.csv`.