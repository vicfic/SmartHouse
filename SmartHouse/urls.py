from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^login/$', views.usuarioLogueado),
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
    url(r'^permisos/$', views.permisos),
    url(r'^modificarFavorito/$', views.modificarFavorito),
    url(r'^modificarDispositivo/$', views.modificarDispositivo),
    url(r'^modificarPermiso/$', views.modificarPermiso),
    url(r'^encenderCalefaccion/$', views.encenderCalefaccion),
    url(r'^obtenerBeacons/$', views.obtenerBeacons),
    url(r'^beacon/$', views.beacon),
]
