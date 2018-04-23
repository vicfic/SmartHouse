import re
import time
import django.conf
from django.utils.deprecation import MiddlewareMixin

class CustomMiddlewareManuel(MiddlewareMixin):
    """
    Middleware para simular una latencia en las peticiones. El valor de dicha 
    latencia viene determinado por la variable "LATENCY_TIME" definida en el 
    archivo "settings.py"
    """
    def process_request(self, request):
        latencia =getattr(django.conf.settings, "LATENCY_TIME", 0)/1000
        print("Peticion recibida... Latencia establecida de "+str(latencia)+" segundos.")
        time.sleep(latencia)
        return  None

    