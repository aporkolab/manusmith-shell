#!/bin/bash

echo "🚀 Starting ManuSmith Shell on macOS..."

# Check if we have the engine dependency
if [ ! -f ~/.m2/repository/org/manusmith/manusmith-engine-plugin/2.0.0/manusmith-engine-plugin-2.0.0.jar ]; then
    echo "⚠️  ManuSmith Engine not found. Installing..."
    
    if [ ! -d "../manusmith-engine" ]; then
        echo "❌ ManuSmith Engine source not found. Please clone the engine repo first:"
        echo "   git clone https://github.com/APorkolab/manusmith-engine ../manusmith-engine"
        exit 1
    fi
    
    cd ../manusmith-engine
    mvn clean package -DskipTests -q
    mvn install:install-file -Dfile=target/manusmith-engine-plugin-2.0.0.jar -DgroupId=org.manusmith -DartifactId=manusmith-engine-plugin -Dversion=2.0.0 -Dpackaging=jar -q
    cd ../manusmith-shell
    echo "✅ ManuSmith Engine installed"
fi

# Build the project
echo "🔨 Building project..."
mvn clean compile package -DskipTests -q

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful"

# Check if JavaFX platform-specific JARs exist
echo "🔍 Checking JavaFX dependencies..."
FX_BASE=~/.m2/repository/org/openjfx/javafx-base/23.0.2/javafx-base-23.0.2-mac-aarch64.jar
FX_GRAPHICS=~/.m2/repository/org/openjfx/javafx-graphics/23.0.2/javafx-graphics-23.0.2-mac-aarch64.jar
FX_CONTROLS=~/.m2/repository/org/openjfx/javafx-controls/23.0.2/javafx-controls-23.0.2-mac-aarch64.jar
FX_FXML=~/.m2/repository/org/openjfx/javafx-fxml/23.0.2/javafx-fxml-23.0.2-mac-aarch64.jar

if [ ! -f "$FX_BASE" ] || [ ! -f "$FX_GRAPHICS" ] || [ ! -f "$FX_CONTROLS" ] || [ ! -f "$FX_FXML" ]; then
    echo "⚠️  JavaFX platform-specific JARs not found. Running dependency resolution..."
    mvn dependency:resolve -q
fi

# Try using the fat JAR with external JavaFX classpath
echo "🎬 Launching application with fat JAR + JavaFX classpath..."
java \
    -Djava.awt.headless=false \
    -Dapple.awt.UIElement=false \
    -Dapple.laf.useScreenMenuBar=true \
    -Dcom.apple.macos.useScreenMenuBar=true \
    -Xdock:name="ManuSmith Shell" \
    -Xmx2048m \
    -cp "target/manusmith-shell-2.0.0-jar-with-dependencies.jar:$FX_BASE:$FX_GRAPHICS:$FX_CONTROLS:$FX_FXML" \
    org.manusmith.shell.MainApp

echo "🏁 Application finished"