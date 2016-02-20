build-and-deploy:
	sbt assembly
	docker build -t certificate-transparency-watch-docker-ct-watch-registry.bintray.io/ct-watch-http-scala .
	docker push certificate-transparency-watch-docker-ct-watch-registry.bintray.io/ct-watch-http-scala
