from flask import Blueprint,jsonify
from flask_restful import Resource, Api, abort, reqparse
from flasgger import swag_from

from . import prometheus

simulator_bp = Blueprint("simulator", __name__)
simulatorApi = Api(simulator_bp)

ORDERS= { "ORD001" : {
     "orderID": "ORD001",
     "customerID": "C01", 
     "productID": "P01", 
     "quantity": 10,  
     "destinationAddress": { "street": "1st main street", "city": "Santa Clara", "country": "USA", "state": "CA", "zipcode": "95051" }
   }}
parser = reqparse.RequestParser()

def getVersion():
  return {"version": "v0.0.1"}
  
class SimulatorResource(Resource):

  def __init__(self):
    print("init")

  @prometheus.track_requests
  #@swag_from("orders.yaml")
  def get(self,orderID):
      if (orderID == None):
        abort(404, message="order {} doesn't exist".format(orderID))
      resp = {"orderID": orderID}
      return jsonify(ORDERS[orderID])

class OrderList(Resource):
  """Orders
  Return the list of orders.
  ---
  responses:
    200:
      description: The orders
      examples:
        status: []
  """
  def get(self):
        return ORDERS

  def post(self):
        args = parser.parse_args()
        order_id = 'ORD%d' % (len(ORDERS) + 1)
        ORDERS[order_id] = {'orderID': args['orderID']}
        return ORDERS[order_id], 201

simulatorApi.add_resource(SimulatorResource, "/api/v1/orders/<orderID>")
# simulatorApi.add_resource(OrderList, "/api/v1/orders/")