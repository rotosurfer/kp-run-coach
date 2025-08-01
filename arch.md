services:
- type: web
  name: spring-backend
  env: java
  buildCommand: ./gradlew build
  startCommand: java -jar build/libs/*.jar
  plan: starter
  envVars:
    - key: DATABASE_URL
      fromDatabase:
      name: app-db
      property: connectionString
    - key: JWT_SECRET
      sync: false
    - key: SPRING_PROFILES_ACTIVE
      value: prod

- type: web
  name: react-frontend
  env: static
  buildCommand: npm install && npm run build
  staticPublishPath: build
  plan: starter

databases:
- name: app-db
  databaseName: app_db
  user: app_user
  plan: starter
