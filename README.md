# AppliMobile
## Objectif
Cette application mobile a pour but de détecter la chute d'une personne et d'envoyer un SMS à toutes les personnes dont le numéro a été rentré dans l'application les alertant de la chute. Dans ce SMS, on retrouve le nom et le prénom de la personne ainsi que sa position géographique.
## Utilisation
Pour commencer, il faut lancer l'application un première fois pour accepter les différentes conditions nécessaires à son utilisation.
Ensuite, on on peut entrer son nom et son prénom dans l'application en appuyant sur le bouton en haut à gauche de la page principale puis en sauvegardant les données rentrées en appuyant sur le bouton "sauvegarder".
Ensuite, il s'agit de rentrer des personnes à contacter qui apparaiteront ensuite sur la page d'accueil de l'application, pour se faire il suffit d'appuyer sur le bouton en haut à droite puis d'entrer les coordonnées de la personne et d'appuyer sur le bouton "sauvegarder". En revenant sur la page principale on peut voir les coordonnées de la personne que nous venons de rentrer.
On peut modifier cette liste de personnes, en effet, en appuyant sur l'une d'entre elles on peut modifier ses coordonnées ou la supprimer.
Pour un fonctionnement optimal de l'application il faut avoir du réseau (permettant d'envoyer un SMS) et ne pas oublier d'activer le paramètre de localisation du téléphone.
Pour finir, pour que l'application fonctionne elle doit être ouverte.
Quand la personne tombe, le SMS est directement envoyé. Une fois la chute passée, et la personne debout, on appuye sur le bouton "réinitialiser" qui est apparu en haut de la page principale.
## Sauvegarde
Les données des contacts sont sauvegardées dans une base de données SQL permettant la sauvegarde de celles-ci après avoir fermé et ouvert à nouveau l'application.

