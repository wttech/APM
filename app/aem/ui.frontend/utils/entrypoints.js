/*
* ========================LICENSE_START=================================
* AEM Permission Management
* %%
* Copyright (C) 2013 Wunderman Thompson Technology
* %%
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* =========================LICENSE_END==================================
*/
const fs = require('fs');


/**
 * Returns all entrypoint chunks (JS and CSS) of the React app. These chunks
 * will not need to be precached because they're already requested from the HTML
 * file)
 *
 * @param {string} assetManifestPath: Path to the asset manifest file from which
 * the entrypoint files can be read
 */
function getEntrypoints(assetManifestPath) {
  if (!fs.existsSync(assetManifestPath)) {
    throw Error(
      `Cannot determine entrypoints: No asset manifest found at path ${assetManifestPath}`
    );
  }
  const manifest = fs.readFileSync(assetManifestPath, { encoding: 'utf8' });
  const manifestContent = JSON.parse(manifest);
  if (!('entrypoints' in manifestContent)) {
    throw Error(
      `Cannot determine entrypoints: Missing "entrypoints" key in ${assetManifestPath}`
    );
  }
  return manifestContent.entrypoints;
}

module.exports = getEntrypoints;
