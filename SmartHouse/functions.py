import json
from google.oauth2 import id_token
from google.auth.transport import requests
from SmartHouse.models import Usuarios
from SmartHouse.models import Favoritos
from SmartHouse.models import Dispositivos
from SmartHouse.models import Lugares
from SmartHouse.models import Roles
from SmartHouse.models import RolesUsuarios
from SmartHouse.models import Rutinas
from SmartHouse.models import Beacons
from SmartHouse.serializers import UsuariosSerializer
from SmartHouse.serializers import FavoritosSerializer
from SmartHouse.serializers import RolesUsuarioSerializer



#Funcion que comprueba el token de un usuario y devuelve su id en la base de datos local
def comprobar_usuario(token_usuario):
    claveWeb = '804708077121-jlt5no06s0e33r1fp732ei8i4k4st5d1.apps.googleusercontent.com'
    try:
        print('Funcion comprobar_usuario\n')        
        responseGoogle = id_token.verify_oauth2_token(token_usuario, requests.Request(), claveWeb)
        #Comprobacion de claves, tienen que coincidir
        if (responseGoogle['aud'] != claveWeb):
            return None
        #Comprobacion si el usuario ya esta dado de alta en la BD
        try:
            usuario = Usuarios.objects.filter(idGoogle=responseGoogle['sub'])
            if(len(usuario)>1): #Hay mas de un usuario con el mismo ID de Google. ERROR
                return None
            if(len(usuario)==1):#Si ya existe en la BD se devuelve el identificador de la BD, no el de Googl
                #Se actualiza el nombre, el email y la imagen 
                update_usr = Usuarios.objects.get(pk=usuario.first().pk)
                update_usr.nombre = responseGoogle['name']
                update_usr.foto = responseGoogle['picture']
                update_usr.email = responseGoogle['email']
                update_usr.save()
                return update_usr.pk
            #Si llegamos hasta aqui, será necesario dar de alta un nuevo usuario en la BD
            constructor = {}
            constructor['idGoogle']=responseGoogle['sub']
            constructor['email']=responseGoogle['email']
            constructor['foto']=responseGoogle['picture']
            constructor['nombre']=responseGoogle['name']
            serializer = UsuariosSerializer(data=constructor)
            if (serializer.is_valid()):
                usuario = serializer.save()
                print("Nuevo usuario dado de alta en la BD. Nombre: ",usuario.nombre,', id: ',usuario.pk)
                roles = Roles.objects.all();
                for rol in roles:
                    constructor = {}
                    constructor['usuarioId']=usuario.id
                    constructor['rolId']=rol.id
                    constructor['activado']= 0
                    serializer = RolesUsuarioSerializer(data=constructor)
                    if (serializer.is_valid()):
                        rol = serializer.save()
                return usuario.pk
            else:
                return None
        except Usuarios.DoesNotExist:
            return None
        return None
    except ValueError:
        #Token invalido
        return None

def detalles_usuario(id_BD):
     usuario = Usuarios.objects.get(pk=id_BD)
     response_data = {}
     response_data['nombre'] = usuario.nombre
     response_data['email'] = usuario.email
     response_data['foto'] = usuario.foto
     response_data['id'] = usuario.pk
     response_data['root'] = usuario.root
     return response_data

def favoritos_usuario(id_usuario):

    response_data = {}
    response_data['usuario_id'] = id_usuario
    
    dispositivos = []
    favoritos = Favoritos.objects.filter(usuarioId=id_usuario)
    for favorito in favoritos:
        favoritoTemp = {}
        favoritoTemp['favorito_id'] = favorito.id
        favoritoTemp['dispositivo_id'] = favorito.dispositivoId.id
        favoritoTemp['dispositivo_nombre'] = favorito.dispositivoId.nombre
        favoritoTemp['dispositivo_activado'] = favorito.dispositivoId.activado
        favoritoTemp['dispositivo_lugar_id'] = favorito.dispositivoId.lugarId.id
        favoritoTemp['dispositivo_tipo_id'] = favorito.dispositivoId.tipoId.id
        dispositivos.append(favoritoTemp)
    response_data['favoritos'] = dispositivos  
    return response_data

def lugares_casa(id_usuario):
    response_data = {}
    array_lugares = []
    
    response_data['usuario_id'] = id_usuario
    lugares = Lugares.objects.all();
    for lugar in lugares:
        lugarTemp = {}
        lugarTemp['id_lugar'] = lugar.id
        lugarTemp['nombre_lugar'] = lugar.nombre
        lugarTemp['photo_lugar'] = lugar.url_photo
        array_lugares.append(lugarTemp)
    response_data['lugares'] = array_lugares
    return response_data

def dispositivos_lugar(id_usuario, id_lugar):

    response_data = {}
    response_data['usuario_id'] = id_usuario
    
    dispositivos = []
    dispositivos_introducidos = []

    dispositivosLugarFavoritos = Favoritos.objects.filter(dispositivoId__lugarId=id_lugar).filter(usuarioId=id_usuario)
    for dispositivoLugar in dispositivosLugarFavoritos:
        dispositivoTemp = {}        
        dispositivoTemp['dispositivo_id'] = dispositivoLugar.dispositivoId.id
        dispositivos_introducidos.append(dispositivoLugar.dispositivoId.id)
        dispositivoTemp['dispositivo_nombre'] = dispositivoLugar.dispositivoId.nombre
        dispositivoTemp['dispositivo_activado'] = dispositivoLugar.dispositivoId.activado
        dispositivoTemp['dispositivo_lugar_id'] =  dispositivoLugar.dispositivoId.lugarId.id
        dispositivoTemp['dispositivo_tipo_id'] = dispositivoLugar.dispositivoId.tipoId.id
        dispositivoTemp['favorito'] = True
        dispositivos.append(dispositivoTemp)

    dispositivosLugar = Dispositivos.objects.filter(lugarId=id_lugar)
    
    for dispositivoLugar in dispositivosLugar:
        flag = True
        for i in dispositivos_introducidos:
            if i==dispositivoLugar.id:
                flag=False;

        if flag:
            dispositivoTemp = {}
            dispositivoTemp['dispositivo_id'] = dispositivoLugar.id
            dispositivoTemp['dispositivo_nombre'] = dispositivoLugar.nombre
            dispositivoTemp['dispositivo_activado'] = dispositivoLugar.activado
            dispositivoTemp['dispositivo_lugar_id'] =  dispositivoLugar.lugarId.id
            dispositivoTemp['dispositivo_tipo_id'] = dispositivoLugar.tipoId.id
            dispositivoTemp['favorito'] = False
            dispositivos.append(dispositivoTemp)
   
    response_data['dispositivos'] = dispositivos  
    return response_data

def modificar_favorito(id_usuario, id_dispositivo, accion):
    if accion:
        favorito = {}
        favorito['usuarioId'] = id_usuario
        favorito['dispositivoId'] = id_dispositivo 
        serializer = FavoritosSerializer(data=favorito)
        if (serializer.is_valid()):
            favorito = serializer.save()
            print("Nuevo favorito en la BD. ID Usuario: ",favorito.usuarioId,', ID Dispositivo: ',favorito.dispositivoId)
            response_data = {}
            response_data['id_favorito'] = favorito.pk
            return response_data
        else:
            response_data = {}
            response_data['result'] = 'error'
            response_data['message'] = 'Some error message'
            return response_data
    else:
        favoritos = Favoritos.objects.filter(usuarioId=id_usuario).filter(dispositivoId=id_dispositivo)
        for favorito in favoritos:
            favoritopk = favorito.id
            favorito.delete()
            print("Borrado favorito en la BD. ID Usuario: ",favorito.usuarioId,', ID Dispositivo: ',favorito.dispositivoId)
        response_data = {}
        response_data['id_favorito'] = favoritopk
        return response_data

def modificar_dispositivo(id_usuario, id_dispositivo, acciones):
    responser_data = {}
    for accion in acciones:
        if accion['tipoAccion'] == 1:  #Acción para apagar y encender dispositivos
            response_data = encenderApagarDispositivo(id_usuario, id_dispositivo, accion['valor'])

    return response_data

def encenderApagarDispositivo(id_usuario, id_dispositivo, accion):

    dispositivo = Dispositivos.objects.get(pk=id_dispositivo)
    roles = RolesUsuarios.objects.filter(usuarioId=id_usuario)

    for rol in roles:
        if rol.rolId.id == dispositivo.grupoPermisos.id:
            if rol.activado:
                #encender o apagar
                if accion:
                    Dispositivos.objects.filter(pk=id_dispositivo).update(activado=True)
                else:
                    Dispositivos.objects.filter(pk=id_dispositivo).update(activado=False)
                response_data = {}
                response_data['error'] = False
                response_data['estado'] = accion
                return response_data

    response_data = {}
    response_data['error'] = True
    response_data['forbidden'] = True
    response_data['estado'] = dispositivo.activado
    return response_data

def lista_usuarios(id_usuario):
    response_data = {}
    array_usuarios = []
    
    response_data['usuario_id'] = id_usuario
    usuarios = Usuarios.objects.all();
    for usuario in usuarios :
        usuarioTemp = {}
        usuarioTemp['id_usuario'] = usuario.id
        usuarioTemp['nombre_usuario'] = usuario.nombre
        usuarioTemp['photo_usuario'] = usuario.foto
        array_usuarios.append(usuarioTemp)
    response_data['usuarios'] = array_usuarios
    return response_data

def obtener_permisos(id_solicitante, id_usuario_permisos):
    response_data = {}
    array_permisos = []
    
    response_data['usuario_id'] = id_solicitante
    response_data['root'] = Usuarios.objects.get(pk=id_solicitante).root

    permisos = RolesUsuarios.objects.filter(usuarioId=id_usuario_permisos)
    for permiso in permisos:
        permisoTemp = {}
        permisoTemp['idPermiso'] = permiso.id
        permisoTemp['nombrePermiso'] = permiso.rolId.nombre
        permisoTemp['activado'] = permiso.activado
        array_permisos.append(permisoTemp)
    response_data['permisos'] = array_permisos
    return response_data


def modificar_permiso(id_usuario, id_permiso, activado):

    if activado:
        RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(pk=id_permiso).update(activado=1)
    else:
        RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(pk=id_permiso).update(activado=0)

    response_data = {}
    response_data['error'] = False
    return response_data

def calefaccion(id_usuario, estadoCalefaccion):

    roles = RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(rolId__nombre='Rutinas')

    if roles[0].activado:

        if estadoCalefaccion:
            estado = 1
        else:
            estado = 0

        rutinas = Rutinas.objects.filter(nombre='Calefaccion')
        for rutina in rutinas:
            dispositivo = Dispositivos.objects.get(pk = rutina.dispositivoId.id)
            roles = RolesUsuarios.objects.filter(usuarioId=id_usuario)
            for rol in roles:
                if rol.rolId.id == dispositivo.grupoPermisos.id:
                    if rol.activado:
                        if dispositivo.activado != estado:
                            Dispositivos.objects.filter(pk=rutina.dispositivoId.id).update(activado=estado)
                    else:
                        response_data = {}
                        response_data['error'] = True
                        response_data['forbidden'] = True
                        response_data['estado'] = dispositivo.activado
                        return response_data

        response_data = {}
        response_data['error'] = False
        return response_data
    else:
        response_data = {}
        response_data['error'] = True
        response_data['forbidden'] = True
        return response_data

def beacons(id_usuario):

    roles = RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(rolId__nombre='Rutinas')

    if roles[0].activado:
        rolLuces = RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(rolId__nombre='Luces')
        if rolLuces[0].activado:

            response_data = {}
            array_beacons = []

            beacons = Beacons.objects.all()
            for beacon in beacons:
                beaconTemp = {}
                beaconTemp['uuid'] = beacon.uuid
                beaconTemp['grupoId'] = beacon.grupoId
                beaconTemp['beaconId'] = beacon.beaconId
                beaconTemp['lugarId'] = beacon.lugarId.id
                beaconTemp['rango'] = beacon.rango
                beaconTemp['nombreLugar'] = beacon.lugarId.nombre
                array_beacons.append(beaconTemp)
            response_data['beacons'] = array_beacons
            response_data['error'] = False
            response_data['usuario_id'] = id_usuario
            return response_data
        else:     
            response_data = {}
            response_data['error'] = True
            response_data['forbidden'] = True
            return response_data
    else:
        response_data = {}
        response_data['error'] = True
        response_data['forbidden'] = True
        return response_data


def beaconAux(id_usuario, uuid, grupoId, beaconId, estado):

    roles = RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(rolId__nombre='Rutinas')

    if roles[0].activado:
        rolLuces = RolesUsuarios.objects.filter(usuarioId=id_usuario).filter(rolId__nombre='Luces')
        if rolLuces[0].activado:
            print(uuid, grupoId, beaconId)
            beacons = Beacons.objects.filter(uuid = uuid).filter(grupoId = grupoId).filter(beaconId = beaconId)
            if estado:
                act = 1
            else:
                act = 0
            Dispositivos.objects.filter(lugarId__id = beacons[0].lugarId.id).filter(tipoId__nombre = 'bombilla').update(activado = act)

            response_data = {}
            response_data['error'] = False
            response_data['usuario_id'] = id_usuario
            return response_data
        else:     
            response_data = {}
            response_data['error'] = True
            response_data['forbidden'] = True
            return response_data
    else:
        response_data = {}
        response_data['error'] = True
        response_data['forbidden'] = True
        return response_data