# SensorWatch - Application Android de Surveillance des Capteurs

## 🎓 Projet Universitaire
Hey ! Bienvenue sur le repo de SensorWatch, un projet universitaire cool qui permet de collecter et d'analyser les données des capteurs de votre téléphone Android. C'est un peu comme avoir un petit labo scientifique dans votre poche ! 📱

### 📊 Fonctionnalités Principales

- **Enregistrement Multi-Capteurs** :
  - Accéléromètre (x, y, z)
  - Gyroscope (x, y, z)
  - Magnétomètre
  - Pression atmosphérique
  - Température
  - Humidité
  - GPS

- **Interface Intuitive** :
  - Navigation facile avec une barre de menu du bas
  - Visualisation en temps réel des données
  - Activation/désactivation individuelle des capteurs
  - Chronométrage des sessions d'enregistrement

- **Stockage et Historique** :
  - Sauvegarde locale des enregistrements
  - Format JSON pour les données
  - Visualisation de l'historique avec durée et date
  - Consultation détaillée des sessions passées

### 🔧 Configuration Technique

- Android 13.0 (API 33) minimum
- Java 11
- Gradle 8.2

### 📱 Installation

1. Clonez le repo :
```bash
git clone https://github.com/RaphHuynh/projetProgm1.git
```

2. Ouvrez le projet dans Android Studio

3. Synchronisez le projet avec Gradle

4. Lancez l'application sur votre appareil Android

### 🤖 Détection de Chute (Module ML)

Le dossier `Detection Chute` contient un système de détection de chute basé sur le machine learning. Pour l'utiliser :

1. Installez les dépendances Python :
```bash
pip install jupyter numpy pandas tensorflow scikit-learn
```

2. Explorez les notebooks :
- `train_and_use_any_model.ipynb` : Entraînement des modèles

3. Lancez le serveur de détection :
```bash
python fall_detection_server.py
```

Les données sont structurées en deux catégories dans les dossiers `Accident/` et `Normal/` pour l'entraînement.

### 🔍 Architecture du Projet

- `app/src/main/` : Code source principal
  - `java/com/example/projprogrammation/` : Classes Java
    - Activités (HomeActivity, SensorActivity, etc.)
    - Services (MqttPublishService)
    - Gestionnaires de données (SensorDataProvider)
  - `res/` : Ressources (layouts, strings, etc.)

---
*Fait avec beaucoup de ☕ par des étudiants*
