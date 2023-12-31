/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.core.intellijthemes;

import java.io.File;

/**
 * @author Karl Tauber
 */
public class IJThemeInfo {
    final String name;
    final String resourceName;
    final boolean dark;
    final String license;
    final String licenseFile;
    final String sourceCodeUrl;
    final String sourceCodePath;
    final File themeFile;
    final String lafClassName;

    public IJThemeInfo(String name, String resourceName, boolean dark,
                       String license, String licenseFile,
                       String sourceCodeUrl, String sourceCodePath,
                       File themeFile, String lafClassName) {
        this.name = name;
        this.resourceName = resourceName;
        this.dark = dark;
        this.license = license;
        this.licenseFile = licenseFile;
        this.sourceCodeUrl = sourceCodeUrl;
        this.sourceCodePath = sourceCodePath;
        this.themeFile = themeFile;
        this.lafClassName = lafClassName;
    }

	public String getName() {
		return name;
	}

	public String getResourceName() {
		return resourceName;
	}

	public boolean isDark() {
		return dark;
	}

	public String getLicense() {
		return license;
	}

	public String getLicenseFile() {
		return licenseFile;
	}

	public String getSourceCodeUrl() {
		return sourceCodeUrl;
	}

	public String getSourceCodePath() {
		return sourceCodePath;
	}

	public File getThemeFile() {
		return themeFile;
	}

	public String getLafClassName() {
		return lafClassName;
	}
}
