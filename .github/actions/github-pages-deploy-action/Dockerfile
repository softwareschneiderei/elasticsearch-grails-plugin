FROM node:18

LABEL "com.github.actions.name"="Deploy to GitHub Pages"
LABEL "com.github.actions.description"="This action will handle the building and deploying process of your project to GitHub Pages."
LABEL "com.github.actions.icon"="git-commit"
LABEL "com.github.actions.color"="orange"

LABEL "repository"="https://github.com/JamesIves/gh-pages-github-action"
LABEL "homepage"="https://github.com/JamesIves/gh-pages-gh-action"
LABEL "maintainer"="James Ives <iam@jamesiv.es>"

ADD entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]