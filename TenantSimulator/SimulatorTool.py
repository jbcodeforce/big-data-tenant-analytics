'''
Simulate clickstream event generation, which includes
- tenant register
- user login
'''
import json
import datetime
import argparse
from flaskClickstream.api import simulator
from cloudevents.http import CloudEvent

def userLoginEvent():
    attributes = {
          "type": "com.anycompany.bdcp.user",
          "source": "https://anycompany.com/user-mgr",
    }
    data = { "eventType": "UserLogin", "username": "bob.the.builder@superemail.com"}
    event = CloudEvent(attributes, data)
    return event

if __name__ == "__main__":
    print("Simulate click stream events - " + str(simulator.getVersion()))
    parser = argparse.ArgumentParser()
    parser.add_argument("filename", help="Must specify a file name")
    parser.add_argument("--append", help="Append records to existing file",action="store_true")
    args = parser.parse_args()
    if args.append:
        print("append to file")
    print(userLoginEvent())