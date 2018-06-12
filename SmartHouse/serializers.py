from rest_framework import serializers
from .models import Usuarios
from .models import Favoritos
from .models import RolesUsuarios


class UsuariosSerializer(serializers.ModelSerializer):
    class Meta:
        model = Usuarios
        fields = ('idGoogle','email','foto','nombre')

class FavoritosSerializer(serializers.ModelSerializer):
    class Meta:
        model = Favoritos
        fields = ('usuarioId','dispositivoId')

class RolesUsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = RolesUsuarios
        fields = ('usuarioId','rolId','activado')