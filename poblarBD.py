import sqlite3

conn = sqlite3.connect('db.sqlite3')
c = conn.cursor()


# Vaciamos las tablas
c.execute("DELETE FROM SmartHouse_rutinasusuarios where id > 0")
c.execute("DELETE FROM SmartHouse_rutinas where id > 0")

c.execute("DELETE FROM SmartHouse_rolesusuarios where id > 0")
c.execute("DELETE FROM SmartHouse_roles where id > 0")

c.execute("DELETE FROM SmartHouse_favoritos where id > 0")
c.execute("DELETE FROM SmartHouse_usuarios where id > 0")


c.execute("DELETE FROM SmartHouse_dispositivos where id > 0")
c.execute("DELETE FROM SmartHouse_lugares where id > 0")
c.execute("DELETE FROM SmartHouse_tipodispositivo where id > 0")

c.execute("DELETE FROM SmartHouse_propiedades where id > 0")



#rutinas
c.execute("INSERT INTO SmartHouse_rutinas VALUES ('1', 'rutina_uno')")
c.execute("INSERT INTO SmartHouse_rutinas VALUES ('2', 'rutina_dos')")
c.execute("INSERT INTO SmartHouse_rutinas VALUES ('3', 'rutina_tres')")
#usuarios
c.execute("INSERT INTO SmartHouse_usuarios VALUES ('1', 'Pepe')")
c.execute("INSERT INTO SmartHouse_usuarios VALUES ('2', 'Luisa')")
c.execute("INSERT INTO SmartHouse_usuarios VALUES ('3', 'Rodrigo')")
#rutinasusuarios (id, estado, usuario, rutina)
c.execute("INSERT INTO SmartHouse_rutinasusuarios VALUES ('1', 'false', '1', '2')")
c.execute("INSERT INTO SmartHouse_rutinasusuarios VALUES ('2', 'false', '2', '3')")
c.execute("INSERT INTO SmartHouse_rutinasusuarios VALUES ('3', 'true', '3', '1')")
c.execute("INSERT INTO SmartHouse_rutinasusuarios VALUES ('4', 'false', '3', '2')")


#roles
c.execute("INSERT INTO SmartHouse_roles VALUES ('1', 'rol_uno')")
c.execute("INSERT INTO SmartHouse_roles VALUES ('2', 'rol_dos')")
c.execute("INSERT INTO SmartHouse_roles VALUES ('3', 'rol_tres')")
#rolesusuarios (id, estado, rol, usuario)
c.execute("INSERT INTO SmartHouse_rolesusuarios VALUES ('1', 'false', '1', '1')")
c.execute("INSERT INTO SmartHouse_rolesusuarios VALUES ('2', 'false', '1', '2')")
c.execute("INSERT INTO SmartHouse_rolesusuarios VALUES ('3', 'true', '2', '2')")
c.execute("INSERT INTO SmartHouse_rolesusuarios VALUES ('4', 'false', '3', '3')")


#lugares
c.execute("INSERT INTO SmartHouse_lugares VALUES ('1', 'lugar_uno')")
c.execute("INSERT INTO SmartHouse_lugares VALUES ('2', 'lugar_dos')")
c.execute("INSERT INTO SmartHouse_lugares VALUES ('3', 'lugar_tres')")
#tipodispositivo
c.execute("INSERT INTO SmartHouse_tipodispositivo VALUES ('1', 'sensor_hmd')")
c.execute("INSERT INTO SmartHouse_tipodispositivo VALUES ('2', 'sensor_tmp')")
c.execute("INSERT INTO SmartHouse_tipodispositivo VALUES ('3', 'bombilla')")
#dispositivos(id, nombre, activado, lugarID, dispositivoId)
c.execute("INSERT INTO SmartHouse_dispositivos VALUES ('1', 'sensor humedad lugar_uno', 'false', '1', '1')")
c.execute("INSERT INTO SmartHouse_dispositivos VALUES ('2', 'sensor temperatura lugar_dos', 'false', '2', '2')")
c.execute("INSERT INTO SmartHouse_dispositivos VALUES ('3', 'bombilla lugar_tres', 'false', '3', '3')")
c.execute("INSERT INTO SmartHouse_dispositivos VALUES ('4', 'sensor humedad lugar_tres', 'false', '3', '1')")

#favoritos(id, dispositivoId, usuarioId)
c.execute("INSERT INTO SmartHouse_favoritos VALUES ('1', '1', '1')")
c.execute("INSERT INTO SmartHouse_favoritos VALUES ('2', '2', '2')")
c.execute("INSERT INTO SmartHouse_favoritos VALUES ('3', '2', '3')")
c.execute("INSERT INTO SmartHouse_favoritos VALUES ('4', '3', '1')")

#propiedades(id, nombre, valor)
c.execute("INSERT INTO SmartHouse_propiedades VALUES ('1', 'propiedad_uno', '/var/vaaalalal')")
c.execute("INSERT INTO SmartHouse_propiedades VALUES ('2', 'propiedad_dos', 'esto vale mucho')")
c.execute("INSERT INTO SmartHouse_propiedades VALUES ('3', 'propiedad_tres', '79847')")


# Save (commit) the changes
conn.commit()

# We can also close the connection if we are done with it.
# Just be sure any changes have been committed or they will be lost.
conn.close()