'''
Generate different data element to build a training set
'''

import argparse, os, random, csv

INDUSTRIES = [ "gov", "health", "retail", "travel","energy","IT","finance","consulting","service" ]
COMPANIES = [ "ABC", "BCD", "CDE", "EFG", "GHI", "HIJ" ]
FIELDS = [ "Company", "Industry", "Revenue", "Employees", "Churn" ]
# in 10  millions
REVENUE_THRESHOLD=100000 

def generateIndustry():
    idx = random.randrange(len(INDUSTRIES))
    return INDUSTRIES[idx]

def generateCompany():
    idx = random.randrange(len(COMPANIES))
    return COMPANIES[idx]

def generateOneRow(idx):
    revenue = 10 * random.randrange(1, round(REVENUE_THRESHOLD / 10))
    employees = round(revenue / ( 1 + random.randrange(1,4)) )
    aRow = ["comp_" + str(idx), generateIndustry(),revenue , employees, 0]
    if aRow[1] in [ "retail", "consulting", "IT"] and aRow[2] < REVENUE_THRESHOLD:
        aRow[4]=1
    return aRow

def writeNrecords(csvwriter,nb_records):
    for idx in range(0,nb_records):
        csvwriter.writerow(generateOneRow(idx))


if __name__ == "__main__":
    print("Generate data to build company risk of churn training set")
    parser = argparse.ArgumentParser()
    parser.add_argument("filename", help="Must specify a file name")
    parser.add_argument("--append", help="Append records to existing file",action="store_true")
    parser.add_argument("--nb_records", help="Specify number of records to generate")
    args = parser.parse_args()
    nb_records = 10
    if args.nb_records:
        nb_records = int(args.nb_records)
    if not os.path.isfile(args.filename) or not args.append:
        with open(args.filename,'w') as csvfile:
            csvwriter = csv.writer(csvfile)
            csvwriter.writerow(FIELDS)
            writeNrecords(csvwriter,nb_records)
            csvfile.close()
    else:
        with open(args.filename,'w') as csvfile:
            csvwriter = csv.writer(csvfile)
            writeNrecords(csvwriter,nb_records)
            csvfile.close()
    print("Done !")