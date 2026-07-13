# LiveHub 📱 — L'appli de ta communauté TikTok

Application Android pour tes abonnés : planning des lives, meilleurs moments,
temps forts, résumé du jour et **notifications automatiques** (rappel 1h avant
chaque live + alerte quand tu publies du nouveau contenu).

## 🚀 Installation (même méthode que MaPiscine)

1. **Crée un repo GitHub PUBLIC** (important : l'appli lit `content.json` via
   raw.githubusercontent.com, qui doit être accessible sans mot de passe).
2. Téléverse tout le contenu de ce dossier dans le repo (branche `main`).
3. **Modifie l'URL** dans `app/src/main/java/com/livehub/app/MainActivity.java` :
   remplace `TON_PSEUDO/TON_REPO` par les tiens.
4. Va dans l'onglet **Actions** → le workflow "Build APK" se lance tout seul
   (ou lance-le avec "Run workflow").
5. Télécharge l'artifact **LiveHub-APK** et installe `app-debug.apk`.
6. Partage l'APK à tes abonnés (lien de téléchargement, groupe, etc.).

## ✏️ Mettre à jour le contenu (depuis ton téléphone)

Tout se passe dans **`content.json`** à la racine du repo :
- `planning` : dates au format `AAAA-MM-JJ HH:MM` (heure de Paris)
- `moments`, `temps_forts`, `resume` : titre + lien TikTok du clip
- `annonce` : le texte de la notification envoyée quand le contenu change

Dès que tu modifies le fichier sur GitHub, les applis de tes abonnés le
détectent (vérification toutes les ~30 min) et envoient la notification.
Pas besoin de recompiler l'APK pour changer le contenu !

## 🔔 Notifications

- **Rappel de live** : envoyé automatiquement quand un live du planning
  démarre dans moins d'1 heure.
- **Nouveau contenu** : envoyé quand `content.json` change (texte = `annonce`).
- Les abonnés doivent accepter la permission notifications au premier lancement.

## ⚠️ Limites

- Vérification périodique (~30 min), pas de push instantané.
- Android peut espacer les vérifications si l'économie de batterie est
  agressive (conseille à tes abonnés d'exclure l'appli de l'optimisation batterie).
