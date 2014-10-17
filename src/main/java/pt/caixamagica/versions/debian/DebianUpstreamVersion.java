/*
 * Copyright 2014 Caixa Mágica Software
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
package pt.caixamagica.versions.debian;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jorge Simões
 */
final class DebianUpstreamVersion implements Comparable<DebianUpstreamVersion>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern PATTERN = Pattern.compile("(\\d+[^~]*)(?:~(\\+?[^\\+]+))?(?:\\+(.+))?");

    private final String version;
    private final String qualifier;
    private final String upgrade;

    DebianUpstreamVersion(String upstreamVersion) {
        Matcher matcher = PATTERN.matcher(upstreamVersion);
        matcher.find();
        version = matcher.group(1);
        qualifier = matcher.group(2) != null ? matcher.group(2) : upstreamVersion.endsWith("~") ? "" : null;
        upgrade = matcher.group(3) != null ? matcher.group(3) : null;
    }

    @Override
    public int compareTo(DebianUpstreamVersion o) {
        int diff = DebianVersionComparator.compare(version, o.version);
        if (diff != 0)
            return diff;

        diff = qualifier != null && o.qualifier != null
                ? DebianVersionComparator.compare(qualifier, o.qualifier)
                : qualifier != null ? -1
                : o.qualifier != null ? 1 : 0;
        if (diff != 0)
            return diff;

        return upgrade != null && o.upgrade != null ? DebianVersionComparator.compare(upgrade, o.upgrade)
                : upgrade != null ? 1 : o.upgrade != null ? -1 : 0;
    }

    String getVersion() {
        return version;
    }

    String getQualifier() {
        return qualifier;
    }

    String getUpgrade() {
        return upgrade;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof DebianUpstreamVersion && compareTo((DebianUpstreamVersion) o) == 0;
    }

    @Override
    public String toString() {
        return version + (qualifier != null ? "~" + qualifier : "") + (upgrade != null ? "+" + upgrade : "");
    }

}
