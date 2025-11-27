// No plugins needed - we're just running shell commands

repositories {
    mavenCentral()
}

// SSL Certificate generation task
tasks.register("generateSslCertificates") {
    group = "docker"
    description = "Generates SSL certificates for nginx proxy (localhost.crt and localhost.key)"
    
    val certsDir = file("docker/nginx/etc/ssl/certs")
    val privateDir = file("docker/nginx/etc/ssl/private")
    val certFile = file("${certsDir}/localhost.crt")
    val keyFile = file("${privateDir}/localhost.key")
    val configFile = file("${layout.buildDirectory.get()}/openssl.conf")
    
    // Create directories if they don't exist
    doFirst {
        certsDir.mkdirs()
        privateDir.mkdirs()
        layout.buildDirectory.get().asFile.mkdirs()
        
        // Check if OpenSSL is available
        try {
            val process = ProcessBuilder("which", "openssl").start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("OpenSSL is not installed or not in PATH. Please install OpenSSL to generate certificates.")
            }
        } catch (e: Exception) {
            throw GradleException("OpenSSL is not installed or not in PATH. Please install OpenSSL to generate certificates.", e)
        }
        
        // Check if certificates already exist
        if (certFile.exists() && keyFile.exists()) {
            logger.warn("SSL certificates already exist. Use 'cleanSslCertificates' task to remove them first.")
            throw StopExecutionException("Certificates already exist. Use 'cleanSslCertificates' to remove them first.")
        }
        
        // Create OpenSSL config file for Subject Alternative Names
        configFile.writeText("""
            [req]
            distinguished_name = req_distinguished_name
            req_extensions = v3_req
            prompt = no
            
            [req_distinguished_name]
            C = BR
            ST = RS
            L = Porto Alegre
            O = Development
            CN = localhost
            
            [v3_req]
            keyUsage = keyEncipherment, dataEncipherment
            extendedKeyUsage = serverAuth
            subjectAltName = @alt_names
            
            [alt_names]
            DNS.1 = localhost
            DNS.2 = *.localhost
            IP.1 = 127.0.0.1
            IP.2 = ::1
        """.trimIndent())
    }
    
    // Generate self-signed certificate
    doLast {
        // Generate certificate using OpenSSL
        val opensslProcess = ProcessBuilder(
            "openssl", "req",
            "-x509",
            "-nodes",
            "-days", "365",
            "-newkey", "rsa:2048",
            "-keyout", keyFile.absolutePath,
            "-out", certFile.absolutePath,
            "-config", configFile.absolutePath,
            "-extensions", "v3_req"
        ).start()
        
        val opensslExitCode = opensslProcess.waitFor()
        if (opensslExitCode != 0) {
            throw GradleException("Failed to generate SSL certificate. OpenSSL exited with code: $opensslExitCode")
        }
        
        // Set proper permissions on private key
        val chmodProcess = ProcessBuilder("chmod", "600", keyFile.absolutePath).start()
        chmodProcess.waitFor() // Ignore exit code for chmod
        
        logger.lifecycle("SSL certificates generated successfully:")
        logger.lifecycle("  Certificate: ${certFile.absolutePath}")
        logger.lifecycle("  Private Key: ${keyFile.absolutePath}")
    }
    
    // Only run if certificates don't exist
    onlyIf {
        !certFile.exists() || !keyFile.exists()
    }
}

// Task to clean/remove SSL certificates
tasks.register<Delete>("cleanSslCertificates") {
    group = "docker"
    description = "Removes generated SSL certificates"
    
    delete(
        "docker/nginx/etc/ssl/certs/localhost.crt",
        "docker/nginx/etc/ssl/private/localhost.key"
    )
    
    doLast {
        logger.lifecycle("SSL certificates removed")
    }
}

// Task to regenerate certificates (clean + generate)
tasks.register("regenerateSslCertificates") {
    group = "docker"
    description = "Regenerates SSL certificates (removes old ones first)"
    
    dependsOn("cleanSslCertificates", "generateSslCertificates")
    
    tasks.findByName("generateSslCertificates")?.mustRunAfter("cleanSslCertificates")
}

// Default task
tasks.register("setup") {
    group = "docker"
    description = "Sets up the project (generates SSL certificates)"
    dependsOn("generateSslCertificates")
}

