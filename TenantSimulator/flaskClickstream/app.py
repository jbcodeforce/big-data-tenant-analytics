from flask import Flask,redirect
from flasgger import Swagger
from api.health import health_bp
from api.prometheus import metrics_bp
from api.simulator import simulator_bp

app = Flask(__name__)


app.register_blueprint(health_bp)
app.register_blueprint(metrics_bp)
app.register_blueprint(simulator_bp)


swagger_template = {
  "swagger": "2.0",
  "info": {
    "title": "Simulator Big Data Control Plane microservice",
    "description": "API for tenant, user, job resource, plus health/monitoring",
    "version": "0.0.1"
  },
  "schemes": [
    "http"
  ],
}
swagger = Swagger(app, template=swagger_template)

@app.route('/')
def index():
    return redirect('/apidocs')

if __name__ == "__main__":
    app.run(debug=True,host='0.0.0.0')