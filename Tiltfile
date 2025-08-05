load("ext://dotenv", "dotenv")
dotenv.load()

k8s_yaml(
    k8s_secret(
        "jobportal-secrets",
        data={
            "POSTGRES_PASSWORD": os.environ.get("POSTGRES_PASSWORD"),
            "JWT_SECRET_KEY": os.environ.get("APPLICATION_SECURITY_JWT_SECRET_KEY"),
        },
    )
)

k8s_yaml(
    [
        "k8s/database.yaml",
        "k8s/redis.yaml",
        "k8s/rabbitmq.yaml",
        "k8s/elasticsearch.yaml",
        "k8s/backend.yaml",
    ]
)

docker_build(
    "my-registry/jobportal-backend",
    "backend/",
    live_update=[sync("backend/target/classes", "/app/classes")],
)

k8s_resource("backend-api", new_name="backend", port_forwards="8080:8080")

k8s_resource("postgres-db", new_name="database")

k8s_resource("redis-cache", new_name="redis")

k8s_resource("rabbitmq-broker", new_name="rabbitmq", port_forwards="15672:15672")

k8s_resource(
    "elasticsearch-search", new_name="elasticsearch", port_forwards="9200:9200"
)
