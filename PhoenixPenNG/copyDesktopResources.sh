#! /bin/bash

RES_ROOT="./../app/src/main/res"
RAW_RES_ROOT="./../app/src/main/res/raw"
DRAWABLE_RES_ROOT="./../app/src/main/res/drawable-nodpi"

JSON_DEST="./../desktop_resources/json"
IMG_DEST="./../desktop_resources/images"
SHADER_DEST="./../desktop_resources/shaders"
TEX_DEST="./../desktop_resources/textures"

echo "> Copying game resources to desktop folder.."

echo "> Copying JSON files.."
find $RAW_RES_ROOT -name "*.json" -exec cp {} $JSON_DEST -v \;

#echo "> Copying shaders.."
#find $RAW_RES_ROOT -name "*.glsl" -exec cp {} $SHADER_DEST -v \;

# Dont copy textures for now, since they differ from the android version!
#echo "> Copying textures.."
#find $DRAWABLE_RES_ROOT -name "*.png" -exec cp {} $TEX_DEST -v \;

echo "> Copying images.."
find $DRAWABLE_RES_ROOT -name "*.bmp" -exec cp {} $IMG_DEST -v \;
