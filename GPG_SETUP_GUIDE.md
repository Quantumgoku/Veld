# 🔐 GPG Setup Guide for Veld Maven Distribution

This guide explains how to configure GPG signing for Maven Central distribution.

## 🚨 Current Status

**Error:** `gpg: no default secret key: No secret key`

This error occurs because GPG signing is enabled but no GPG key is configured in the environment.

## ⚡ Quick Solutions

### Option 1: Disable GPG (Development/Testing)
```bash
# Build without GPG signing
mvn clean install -DskipGpg=true

# Current pom.xml is configured with skipGpg=true by default
# This should work for local development
```

### Option 2: Configure GPG Key (Production)
Required for Maven Central publication.

#### Step 1: Generate GPG Key
```bash
# Use the provided script
./gpg-manager.sh generate "Your Name" "your@email.com"

# Or manually
gpg --gen-key
```

#### Step 2: Get Key ID
```bash
gpg --list-secret-keys --keyid-format LONG
```

#### Step 3: Export Key for CI/CD
```bash
# Export private key
gpg --armor --export-secret-keys YOUR_KEY_ID

# Export public key
gpg --armor --export YOUR_KEY_ID
```

#### Step 4: Configure GitHub Secrets
Add these secrets to your GitHub repository:

- **GPG_KEYNAME**: Your GPG key ID
- **GPG_PASSPHRASE**: Your GPG key passphrase
- **GPG_PRIVATE_KEY**: Your private key (armored format)

#### Step 5: Enable GPG in pom.xml
```xml
<properties>
    <skipGpg>false</skipGpg>  <!-- Change from true to false -->
</properties>
```

## 📝 Detailed GPG Configuration

### Maven GPG Plugin Configuration
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-gpg-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <skip>${skipGpg}</skip>
        <keyname>${env.GPG_KEYNAME}</keyname>
        <passphraseServerId>${gpg.keyname}</passphraseServerId>
        <gpgArguments>
            <arg>--no-tty</arg>
            <arg>--pinentry-mode</arg>
            <arg>loopback</arg>
        </gpgArguments>
    </configuration>
    <executions>
        <execution>
            <id>sign-artifacts</id>
            <phase>verify</phase>
            <goals>
                <goal>sign</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Maven Settings Configuration
```xml
<servers>
    <server>
        <id>gpg.passphrase</id>
        <passphrase>${env.GPG_PASSPHRASE}</passphrase>
    </server>
</servers>
```

## 🔧 Automated Setup

Use the provided GPG manager script:
```bash
# Generate new key
./gpg-manager.sh generate "Veld Release" "releases@veld.com"

# Export key for GitHub
./gpg-manager.sh export

# Check key status
./gpg-manager.sh status
```

## 🚀 Release Process

### With GPG Enabled:
```bash
# 1. Ensure GPG key is configured
# 2. Build and sign
./release.sh 1.0.0 1.0.1-SNAPSHOT

# Or manually
mvn clean deploy -DskipGpg=false
```

### Without GPG (Development):
```bash
# Local development build
mvn clean install -DskipGpg=true

# Skip signing in CI/CD
mvn clean deploy -DskipGpg=true
```

## ⚠️ Important Notes

1. **Maven Central Requirement**: GPG signing is mandatory for Maven Central publication
2. **Key Security**: Never commit GPG keys to version control
3. **Key Backup**: Always backup your GPG key securely
4. **Environment Variables**: GPG settings must be configured in CI/CD environment

## 🐛 Troubleshooting

### Error: "No secret key"
- Generate a new GPG key using `./gpg-manager.sh generate`
- Or configure existing key in environment

### Error: "Bad passphrase"
- Check `GPG_PASSPHRASE` environment variable
- Verify key passphrase is correct

### Error: "Key not found"
- Check `GPG_KEYNAME` environment variable
- Ensure key ID matches the actual GPG key

## 📊 Current Configuration

- **Default**: GPG signing disabled (`skipGpg=true`)
- **Production**: Enable with `skipGpg=false`
- **CI/CD**: Configure via GitHub secrets
- **Local**: Can override with `-DskipGpg=true/false`