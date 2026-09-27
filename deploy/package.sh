#!/usr/bin/env bash
set -euo pipefail

# =================================================================================================
# Packages the production server into dist/: the fat JAR, app.jar, beside the static assets it
# serves, which the Dockerfile copies into the image as they are. CI runs this on main, from the
# build it has just tested, so that the Deploy workflow need not build anything again.
#
# It asks an sbt server for the build, so run it where one is running to reuse what that server
# has built, or anywhere else to start one.
# =================================================================================================

sbt --client assemble

rm -rf dist
mkdir dist
mv app.jar dist/
cp -r "$(find target -type d -path '*/resource_managed/main/assets' | head -1)" dist/assets
