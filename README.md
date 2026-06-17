Diario de Kanto - API REST

Este repositorio contiene el motor backend y la lógica de negocio para "El Diario de Kanto". Está construido bajo una arquitectura de microservicios monolítica enfocada en proporcionar endpoints seguros y eficientes para gestionar noticias, usuarios, comentarios y la base de datos de la Pokédex.

Puedes ver el Frontend que consume esta API y la interfaz gráfica funcionando en el siguiente repositorio: (https://github.com/Zauk99/ELdK)

Stack Técnico:

Lenguaje Core: Java 17+

Framework: Spring Boot

Seguridad: Spring Security (Gestión de autenticación y roles)

Persistencia: Spring Data JPA / Hibernate

Base de Datos:  PostgreSQL 

Contenedores: Docker y Docker Compose

Docker y Despliegue:
El entorno de base de datos y la aplicación están preparados para ser contenerizados. El repositorio incluye un archivo docker-compose.yml para levantar la infraestructura de forma automatizada.
Para inicializar la base de datos en local, basta con situarse en la raíz del proyecto y ejecutar:
docker-compose up -d

Endpoints Principales (Ejemplos):
La API expone múltiples rutas para la gestión del CRUD de las distintas entidades de la base de datos. Algunos ejemplos destacados:

Noticias:
GET /api/noticias -> Obtiene el listado completo de noticias.
GET /api/noticias/{id} -> Devuelve el detalle de un artículo específico.

Usuarios y Autenticación:
POST /api/usuarios/registro -> Registra un nuevo usuario en la plataforma.
GET /api/usuarios/perfil/{id} -> Obtiene los datos públicos de un entrenador.

Pokédex:
GET /api/pokemon -> Devuelve la lista paginada de criaturas.
GET /api/pokemon/{nombre} -> Filtra y devuelve la información de un Pokémon.

Cómo ejecutar el proyecto en local:

Clona el repositorio.

Ejecuta el archivo docker-compose.yml para levantar el contenedor de la base de datos.

Revisa el archivo application.properties para asegurar que las credenciales de la base de datos coinciden.

Ejecuta ApiApplication.java desde tu IDE.
