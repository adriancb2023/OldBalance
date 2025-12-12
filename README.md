# 🏋️‍♂️ OldBalance

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Status](https://img.shields.io/badge/Status-Beta-orange?style=for-the-badge)

> **Tu compañero ideal para el control de peso.**
> *Simple, moderno y eficiente.*

---

## 📖 Descripción

**OldBalance** es una aplicación nativa de Android diseñada para ayudarte a alcanzar tus objetivos de peso de manera efectiva. Con una interfaz limpia construida en **Jetpack Compose**, ofrece una experiencia de usuario fluida y moderna.

### ✨ Características Principales

*   **📊 Gráficas Interactivas:** Visualiza tu progreso con gráficos detallados (powered by MPAndroidChart).
*   **🎯 Metas Personalizadas:** Define y sigue tus objetivos de peso.
*   **📝 Registro Diario:** Bitácora fácil de usar para tus registros diarios.
*   **💾 Historial Completo:** Almacenamiento local seguro con Room Database.
*   **🎨 Diseño Moderno:** UI/UX optimizada con Material Design.

---

## 🛠️ Tecnologías

Este proyecto está construido con las últimas herramientas de desarrollo Android:

*   **[Kotlin](https://kotlinlang.org/)**: Lenguaje principal.
*   **[Jetpack Compose](https://developer.android.com/jetbrains/compose)**: Modern toolkit para UI nativa.
*   **[Room Database](https://developer.android.com/training/data-storage/room)**: Persistencia de datos robusta.
*   **[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)**: Librería para visualización de datos.
*   **Coroutines & Flow**: Para manejo de asincronía y reactividad.

---

## 📸 Capturas de Pantalla

| Inicio | Gráfica | Historia |
|:---:|:---:|:---:|
| ![Home](https://via.placeholder.com/200x400?text=Home) | ![Graph](https://via.placeholder.com/200x400?text=Graph) | ![History](https://via.placeholder.com/200x400?text=History) |

*(Próximamente: Capturas reales de la aplicación)*

---

## 🚀 Instalación y Uso

1.  **Clonar el repositorio**
    ```bash
    git clone https://github.com/USUARIO/OldBalance.git
    ```
2.  **Abrir en Android Studio**
    *   Sincronizar el proyecto con Gradle.
3.  **Compilar y Ejecutar**
    *   Conecta tu dispositivo o usa un emulador.
    *   Ejecuta la tarea `Run 'app'`.

O descarga el APK directamente desde [Releases](../../releases).

---

## 💻 Ejemplo de Código

Así es como gestionamos los registros de peso con Room:

```kotlin
@Entity(tableName = "weights")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val weight: Float
)
```

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si tienes ideas para mejorar la app:
1.  Haz un Fork del proyecto.
2.  Crea una rama (`git checkout -b feature/nueva-feature`).
3.  Commit tus cambios (`git commit -m 'Añadir nueva feature'`).
4.  Push a la rama (`git push origin feature/nueva-feature`).
5.  Abre un Pull Request.

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT** (o la que se decida aplicar). Consulta el archivo `LICENSE` para más detalles.

---
<p align="center">
  Hecho con ❤️ por Adrián
</p>
