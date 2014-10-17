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
final class DebianRevision implements Comparable<DebianRevision>, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern PATTERN = Pattern.compile("([\\.\\p{Alnum}][^~]*)(?:~(.+))?");

    private final String revision;
    private final String release;

    DebianRevision(String debianRevision) {
        Matcher matcher = PATTERN.matcher(debianRevision);
        matcher.find();
        revision = matcher.group(1);
        release = matcher.group(2) != null ? matcher.group(2) : debianRevision.endsWith("~") ? "" : null;
    }

    @Override
    public int compareTo(DebianRevision o) {
        int diff = revision != null && o.revision != null ? DebianVersionComparator.compare(revision, o.revision)
                : revision != null ? -1 : o.revision != null ? 1 : 0;
        if (diff != 0)
            return diff;
        return release != null && o.release != null ? DebianVersionComparator.compare(release, o.release)
                : release != null ? -1 : o.release != null ? 1 : 0;
    }

    String getRevision() {
        return revision;
    }

    String getRelease() {
        return release;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof DebianRevision && compareTo((DebianRevision) o) == 0;
    }

    @Override
    public String toString() {
        return revision + (release != null ? "~" + release : "");
    }

}
