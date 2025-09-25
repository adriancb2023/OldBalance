# 🏋️‍♂️ OldBalance  

![Status](https://img.shields.io/badge/status-beta-orange)  
![Tech](https://img.shields.io/badge/made%20with-Kotlin-blueviolet)  
![Database](https://img.shields.io/badge/DB-Room-green)  
[![Download](https://img.shields.io/github/downloads/USUARIO/OldBalance/total?label=📦%20Descargas)](../../releases)  

> ⚠️ **IMPORTANTE:** Reemplaza `USUARIO` en el enlace del badge por tu nombre de usuario de GitHub para que funcione bien.

---

## 📑 Tabla de Contenidos
- [📖 Descripción](#-descripción)  
- [🚀 Tecnologías](#-tecnologías)  
- [📥 Instalación](#-instalación)  
- [📌 Estado del proyecto](#-estado-del-proyecto)  
- [📷 Capturas de pantalla](#-capturas-de-pantalla)  
- [💻 Ejemplo de uso](#-ejemplo-de-uso)  
- [🤝 Contribuir](#-contribuir)  
- [📜 Licencia](#-licencia)  

## 📖 Descripción  
**OldBalance** es una aplicación móvil para el **control de peso**.  
Con ella puedes:  
- 📊 Visualizar gráficas de tu progreso  
- 🎯 Definir objetivos de peso  
- 📝 Registrar tu peso diario  
- 📂 Revisar tu historial  

## 🚀 Tecnologías  
- [Kotlin](https://kotlinlang.org/)  
- [Room (Android Persistence Library)](https://developer.android.com/training/data-storage/room)  

## 📥 Instalación  
1. Descarga la última versión desde la sección [Releases](../../releases).  
2. Instala el archivo `.apk` en tu dispositivo Android.  
3. ¡Listo! Ya puedes comenzar a usar OldBalance.  

## 📌 Estado del proyecto  
✅ **Terminado**  
🔍 Actualmente en fase de búsqueda y corrección de bugs.  

## 📷 Capturas de pantalla  
_(Aquí puedes añadir imágenes o GIFs de la app en funcionamiento)_  

```markdown
![Demo](ruta/a/tu/captura.png)
```

## 💻 Ejemplo de uso  

Ejemplo en **Kotlin** con **Room** para registrar un nuevo peso en la base de datos:  

```kotlin
@Entity(tableName = "weights")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val weight: Float
)

@Dao
interface WeightDao {
    @Insert
    suspend fun insertWeight(weight: WeightEntry)

    @Query("SELECT * FROM weights ORDER BY date DESC")
    suspend fun getAllWeights(): List<WeightEntry>
}

// Uso en un ViewModel o Repository
val newEntry = WeightEntry(
    date = System.currentTimeMillis(),
    weight = 72.5f
)

CoroutineScope(Dispatchers.IO).launch {
    weightDao.insertWeight(newEntry)
}
```

## 🤝 Contribuir  
Si encuentras algún bug o tienes una mejora:  
- Abre un **issue** en este repo.  
- O crea un **pull request** con tu propuesta.  

## 📜 Licencia  
Este proyecto **no tiene licencia definida** por el momento.  
