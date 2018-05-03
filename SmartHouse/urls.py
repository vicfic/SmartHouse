from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^login/$', views.index),
    url(r'^propiedades/$', views.propiedades),
    url(r'^rutinas/$', views.rutinas),
    url(r'^usuarios/$', views.usuarios),
    url(r'^rutinasusuarios/$', views.rutinasusuarios),
    url(r'^roles/$', views.roles),
    url(r'^rolesusuarios/$', views.rolesusuarios),
    url(r'^lugares/$', views.lugares),
    url(r'^tipodispositivo/$', views.tipodispositivo),
    url(r'^dispositivos/$', views.dispositivos),
    url(r'^favoritos/$', views.favoritos),
]
