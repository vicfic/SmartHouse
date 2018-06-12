import json
from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from django.http import HttpResponse
from django.template import Context, loader

from django.shortcuts import render, get_object_or_404, redirect
from django.core.paginator import Paginator, EmptyPage, PageNotAnInteger
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.contrib.auth import logout
from django.http import HttpResponseRedirect
from django.db import transaction
#from .forms import UserForm,ProfileForm
from google.oauth2 import id_token
from google.auth.transport import requests

from SmartHouse.models import Propiedades, Rutinas, Usuarios, RutinasUsuarios, Roles, RolesUsuarios, Lugares, TipoDispositivo, Dispositivos, Favoritos
from django.core import serializers
from .functions import comprobar_usuario
from .functions import detalles_usuario
from .functions import favoritos_usuario
from .functions import lugares_casa
from .functions import dispositivos_lugar
from .functions import modificar_favorito
from .functions import modificar_dispositivo
from .functions import lista_usuarios
from .functions import obtener_permisos
from .functions import modificar_permiso
from .functions import calefaccion
from .functions import beacons
from .functions import beaconAux

#Funcion para dar de alta un usuario o comprobar su ID
@csrf_exempt
def usuarioLogueado(request):
    
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)

    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        response_data = detalles_usuario(id_usuario)
        print(response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print(response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

def propiedades(request):
    propiedades = Propiedades.objects.all()
    data = serializers.serialize('json', propiedades)

    return HttpResponse(data, content_type='application/json')


def rutinas(request):
    rutinas = Rutinas.objects.all()
    data = serializers.serialize('json', rutinas)

    return HttpResponse(data, content_type='application/json')




def rutinasusuarios(request):
    rutinasusuarios = RutinasUsuarios.objects.all()
    data = serializers.serialize('json', rutinasusuarios)

    return HttpResponse(data, content_type='application/json')

def roles(request):
    roles = Roles.objects.all()
    data = serializers.serialize('json', roles)

    return HttpResponse(data, content_type='application/json')

def rolesusuarios(request):
    rolesusuarios = RolesUsuarios.objects.all()
    data = serializers.serialize('json', rolesusuarios)

    return HttpResponse(data, content_type='application/json')



def tipodispositivo(request):
    tipodispositivo = TipoDispositivo.objects.all()
    data = serializers.serialize('json', tipodispositivo)

    return HttpResponse(data, content_type='application/json')


#Función para obtener los dispositivos de un lugar de la casa
@csrf_exempt
def dispositivos(request):
    print(request.method)
    print(request.path_info)

    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])

    if id_usuario is not None:
        id_lugar = received_json_data['lugarId'] 
        response_data = dispositivos_lugar(id_usuario, id_lugar)
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Función para obtener los lugares de la casa
@csrf_exempt
def lugares(request):
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        response_data = lugares_casa(id_usuario);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")


#Funcion para obtener los favoritos de un usuario concreto
@csrf_exempt
def favoritos(request):
    print(request.method)
    print(request.path_info)

    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        response_data = favoritos_usuario(id_usuario)
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Funcion para modificar un favorito
@csrf_exempt
def modificarFavorito(request):
    print(request.method)
    print(request.path_info)

    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        id_dispositivo = received_json_data['dispositivoId'] 
        accion = received_json_data['accion'] 
        response_data = modificar_favorito(id_usuario, id_dispositivo, accion)
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Funcion para modificar un dispositivo concreto
@csrf_exempt
def modificarDispositivo(request):
    print(request.method)
    print(request.path_info)

    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        id_dispositivo = received_json_data['dispositivoId'] 
        acciones = received_json_data['acciones'] 
        response_data = modificar_dispositivo(id_usuario, id_dispositivo, acciones)
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Funcion para obtener la lista de usuarios
@csrf_exempt
def usuarios(request):

    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        response_data = lista_usuarios(id_usuario);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Funcion para obtener la lista de permisos de un usuario
@csrf_exempt
def permisos(request):

    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario que solicita la petición esta en la BD, si no esta lo crea
    id_solicitante = comprobar_usuario(received_json_data['tokenId'])
    if id_solicitante is not None:
        id_usuario_permisos = received_json_data['usuarioId'] 
        response_data = obtener_permisos(id_solicitante, id_usuario_permisos);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")


#Funcion para modificar un permiso
@csrf_exempt
def modificarPermiso(request):
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario que solicita la petición esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        permisoId = received_json_data['permisoId'] 
        activado = received_json_data['activado'] 
        response_data = modificar_permiso(id_usuario, permisoId, activado);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

#Funcion para encender/apagar la calefaccion
@csrf_exempt
def encenderCalefaccion(request):
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario que solicita la petición esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        estadoCalefaccion = received_json_data['estado'] 
        response_data = calefaccion(id_usuario, estadoCalefaccion);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")

@csrf_exempt
def obtenerBeacons(request):
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario que solicita la petición esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        response_data = beacons(id_usuario);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")


@csrf_exempt
def beacon(request):
    print(request.method)
    print(request.path_info)
    
    received_json_data=json.loads(request.body)
    #Funcion auxiliar que comprueba que el usuario que solicita la petición esta en la BD, si no esta lo crea
    id_usuario = comprobar_usuario(received_json_data['tokenId'])
    if id_usuario is not None:
        uuid = received_json_data['uuid'] 
        grupoId = received_json_data['grupoId'] 
        beaconId = received_json_data['beaconId'] 
        estado = received_json_data['estado'] 
        response_data = beaconAux(id_usuario, uuid, grupoId, beaconId, estado);
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")
    else:
        response_data = {}
        response_data['result'] = 'error'
        response_data['message'] = 'Some error message'
        print('RESPUESTA ENVIADA:    ',response_data)
        return HttpResponse(json.dumps(response_data), content_type="application/json")