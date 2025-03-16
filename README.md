# InstantLike - Application de Partage de Photos

## Description du Projet

InstantLike est une application Android de partage de photos inspirée des réseaux sociaux modernes. Elle permet aux utilisateurs de créer un profil, de publier des photos, d'interagir avec les publications des autres utilisateurs via des likes et des commentaires, et de communiquer par messages privés.

## Fonctionnalités Principales

### Authentification
- Création de compte avec email, mot de passe et nom d'utilisateur
- Connexion sécurisée avec Firebase Authentication
- Récupération de mot de passe par email
- Photo de profil personnalisée

### Publications
- Prise de photo via l'appareil photo du téléphone
- Sélection d'images depuis la galerie
- Ajout d'un titre et d'une description aux publications
- Affichage des publications dans un fil d'actualité

### Interactions Sociales
- Like des publications
- Commentaires sur les publications
- Partage de publications
- Affichage du nombre de likes

### Messagerie
- Liste des utilisateurs disponibles pour conversation
- Conversations privées en temps réel
- Historique des messages
- Affichage des messages par date et heure

### Profil Utilisateur
- Affichage des informations personnelles
- Galerie des publications de l'utilisateur
- Statistiques (nombre de publications)
- Déconnexion sécurisée

## Architecture Technique

### Structure du Projet
L'application est organisée en packages selon les fonctionnalités:

- **Adapter**: Gestion de l'affichage des listes (RecyclerView)
  - `ComAdapter`: Affichage des commentaires
  - `ImageAdapter`: Affichage des images dans le fil d'actualité
  - `MPAdapter`: Affichage des utilisateurs pour la messagerie
  - `PublicationAdapter`: Affichage des publications de l'utilisateur

- **Connection**: Gestion de l'authentification
  - `Login`: Connexion utilisateur
  - `Register`: Création de compte

- **Image**: Traitement des données d'images
  - `ImageData`: Modèle de données pour les images

- **message**: Système de messagerie
  - `Message`: Modèle de message
  - `MessageAdapter`: Affichage des messages
  - `MessageEntreUtilisateur`: Interface de conversation
  - `MyFirebaseDatabase`: Gestion de la base de données des messages
  - `UtilisateurMP`: Liste des utilisateurs pour la messagerie

- **Poste**: Gestion des publications
  - `CreationPoste`: Interface de création de publication
  - `InfoPoste`: Détails d'une publication

- **Profil**: Gestion du profil utilisateur
  - `ProfilInfo`: Affichage et gestion du profil

### Technologies Utilisées

- **Firebase**:
  - Authentication: Gestion des utilisateurs
  - Firestore: Base de données NoSQL
  - Storage: Stockage des images
  - Realtime Database: Messagerie en temps réel

- **Bibliothèques**:
  - Picasso & Glide: Chargement et mise en cache des images
  - EasyPermissions: Gestion des permissions Android

## Installation et Configuration

1. Cloner le projet depuis le dépôt
2. Ouvrir le projet dans Android Studio
3. Configurer un projet Firebase:
   - Créer un projet dans la console Firebase
   - Ajouter une application Android et suivre les instructions
   - Télécharger le fichier `google-services.json` et le placer dans le dossier `app`
4. Synchroniser le projet avec Gradle
5. Exécuter l'application sur un émulateur ou un appareil physique

## Captures d'écran

*Des captures d'écran de l'application pourraient être ajoutées ici pour illustrer les fonctionnalités principales.*

## Améliorations Possibles

- Ajout de fonctionnalités de recherche
- Implémentation de stories éphémères
- Système de hashtags pour catégoriser les publications
- Amélioration de l'interface utilisateur
- Optimisation des performances et de la consommation de données
- Support hors ligne avec synchronisation

## Sécurité

L'application utilise les meilleures pratiques de sécurité Firebase:
- Authentification sécurisée
- Règles de sécurité pour Firestore et Storage
- Validation des données côté serveur
- Protection contre les injections
