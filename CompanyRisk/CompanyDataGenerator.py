'''
Generate different data element to build a training set
'''

import argparse, os, random, csv
#import numpy as np

INDUSTRIES = [ "gov", "health", "retail", "travel","energy","IT","finance","consulting","service" ]
COMPANIES = [ "ABC", "BCD", "CDE", "EFG", "GHI", "HIJ" ]
FIELDS = [ "Company", "Industry", "Revenue", "Employees", "#job30", "#job90", "MonthlyCharge", "TotalCharge","Churn" ]
# in 10  millions
REVENUE_THRESHOLD=100000 
random.seed(2)

def generateIndustry():
    idx = random.randrange(len(INDUSTRIES))
    return INDUSTRIES[idx]

def generateCompany():
    idx = random.randrange(len(COMPANIES))
    return COMPANIES[idx]

def generateOneRow(idx):
    revenue = 10 * random.randrange(1, round(REVENUE_THRESHOLD / 10))
    employees = round(revenue / ( 1 + random.randrange(1,4)) )
    job30 =  random.randrange(3, 10)
    job90 =  random.randrange(10, 100)
    MonthlyCharge = random.randrange(100, 1200)
    TotalCharge = random.randrange(MonthlyCharge, random.randrange(MonthlyCharge + 10 ,MonthlyCharge*12))
    aRow = ["comp_" + str(idx), generateIndustry(),revenue , employees, job30, job90,MonthlyCharge,TotalCharge,0]
    if aRow[1] in [ "retail", "consulting", "IT"] and aRow[2] < REVENUE_THRESHOLD:
        aRow[8]=1
    if aRow[4] < 5 or aRow[5] < 20:
        aRow[8]=1
    return aRow

def writeNrecords(csvwriter,nb_records):
    for idx in range(0,nb_records):
        csvwriter.writerow(generateOneRow(idx))


if __name__ == "__main__":
    print("Generate data to build company risk of churn training set")
    parser = argparse.ArgumentParser()
    parser.add_argument("filename", help="Must specify a file name", default="companies.csv")
    parser.add_argument("--nb_records", help="Specify number of records to generate",type=int,default=10)
    args = parser.parse_args()

    with open(args.filename,'w') as csvfile:
        csvwriter = csv.writer(csvfile)
        csvwriter.writerow(FIELDS)
        writeNrecords(csvwriter,args.nb_records)
        csvfile.close()
    print("Done !")