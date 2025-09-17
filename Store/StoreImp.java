package Store;


/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Generic store implementation
 *
 * @author <a href="mailto:nir@verisoft.co">Nir Gallner</a>
 * @see Store
 * @see StoreManager
 * @see StoreType
 * @since 0.0.2 (Jan 2022)
 */

public class StoreImp implements Store {
    private final Map<Object, Object> store = new ConcurrentHashMap<>();
    private static Logger logger= LoggerFactory.getLogger(StoreImp.class);

    @Override
    public <T> T getValueFromStore(Object key) {
        Object obj = store.get(key);
        logger.debug("Retrieved " + obj + " from store using key " + key);
        return (T) obj;
    }

    @Override
    public void putValueInStore(Object key, Object value) {
        store.put(key, value);
        logger.debug("Insert into store KEY: " + key + "\tVALUE: " + value);
    }

    @Override
    public void removeValueFromStore(Object key) {
        store.remove(key);
        logger.debug("Removed value from store using key " + key);
    }
}
