## Cloudator Files

Cloudator Files es un servicio secundario que maneja las operaciones de almacenamiento de archivos. 
Este recibe las subidas de archivos del servidor principal y las almacena en el disco, luego confirma el estado de estas operaciones al servidor principal.

### Características

- **Almacenamiento de Archivos**: Maneja el almacenamiento real de archivos en el disco.
- **Confirmación de Estado**: Se comunica con el servidor principal para confirmar el éxito o fracaso de las operaciones de archivos.

### Estructura del Proyecto

- **Backend**: Desarrollado con Java Spring Boot, responsable de la gestión de archivos y la comunicación entre servidores.

### Configuración
En el archivo de `src/main/resources/application.properties` se encuentra la configuración del proyecto, estos parámetros deben ser editados:

- `spring.servlet.multipart.max-file-size` Se debe poner el tamaño maximo que puede pasar por el servidor (EN GB)
- `spring.servlet.multipart.max-request-size` Se debe poner el tamaño maximo que puede pasar por el servidor (EN GB)
- `secretkey` Se utiliza para la encriptación y desencriptación, debe ser diferente a `secretencryptor` pero igual que en el servidor de [api](https://github.com/BakerIsCoding/cloudator)
- `secretencryptor` Se utiliza para la encriptación y desencriptación, debe ser diferente a `secretkey` pero igual que en el servidor de [api](https://github.com/BakerIsCoding/cloudator)
- `directory` Es el directorio donde se guardarán todos los archivos

### Instalación

1. **Clonar el Repositorio**:
   ```bash
   git clone https://github.com/BakerIsCoding/cloudator-files.git
   cd cloudator-files
    ```
2. Construir el Proyecto:
Usar Maven para construir el proyecto.
   ```bash
    ./mvnw clean install
    ``` 
3. Ejecutar la Aplicación:
Iniciar la aplicación de Spring Boot.
   ```bash
    ./mvnw spring-boot:run
    ```

### Licencia

Este proyecto está licenciado bajo la Licencia MIT.

# Otros Repositorios del Proyecto Cloudator

Puedes revisar los demás repositorios aquí:

- **Cloudator - API:** Aplicación que se encarga de mostrar las vistas y comunicarse con el servidor de archivos.  
  [https://github.com/BakerIsCoding/cloudator](https://github.com/BakerIsCoding/cloudator)

- **Cloudator - FILES:** Aplicación que se encarga de escuchar al servidor de API, y de iniciar una descarga cuando el usuario la necesita.  
  [https://github.com/BakerIsCoding/cloudator-files](https://github.com/BakerIsCoding/cloudator-files)

- **Cloudator-Android:** Aplicación móvil.  
  [https://github.com/BakerIsCoding/Android-Cloudator-App/](https://github.com/BakerIsCoding/Android-Cloudator-App/)

# Autores del Proyecto Cloudator

Este proyecto ha sido desarrollado por los siguientes autores:

- **[Cramcat639](https://github.com/Cramcat639)**
- **[EdNuGa](https://github.com/EdNuGa)**
- **[BakerIsCoding](https://github.com/BakerIsCoding)**
