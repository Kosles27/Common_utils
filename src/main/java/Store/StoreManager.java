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

/**
 * Global thread safe storage for objects. It is implemented as a singletone.<br>
 * <b>Example:</b><br>
 * The following example is taken from the Store unit test, and provides a basic use of the store.
 * This example uses GLOBAL store, however similar code can be applied to LOCAL_THREAD store. <br>
 * <pre>
 * {@code
 *         String name = "Nir";
 *         StoreManager.getStore(StoreType.GLOBAL).putValueInStore("NIR", name);
 *         String receivedName = StoreManager.getStore(StoreType.GLOBAL).getValueFromStore("NIR");
 *         Assertions.assertEquals(name, receivedName, "Should have retrieved from store");
 *  }
 * </pre>
 *
 * @author <a href="mailto:nir@verisoft.co">Nir Gallner</a>
 * @see Store
 * @see StoreImp
 * @see StoreType
 * @since 0.0.2 (Jan 2022)
 */

public class StoreManager {

    private static final Map<Long, Store> storeMap = new HashMap<>();
    private static Logger logger= LoggerFactory.getLogger(StoreManager.class);

    private StoreManager() {
    }


    /**
     * Retrieve store from the store object, according to store type selected.
     * If there is no store, which previously registered, it will create a new store and retrieve it
     *
     * @param storeType Type of store to retrieve (local by thread / global to all threads)
     * @return Store object, according to type selected
     */

    public static synchronized Store getStore(StoreType storeType) {
        Long threadId = (storeType == StoreType.LOCAL_THREAD) ?
                Thread.currentThread().getId() : 0;

        // Show only global access to store
        if (threadId == 0){
            logger.debug("Get store of thread " + threadId + " (0 means global)");
        }
        else
            logger.trace("Get store of thread " + threadId + " (0 means global)");


        // If store not exist, create a store
        if (storeMap.get(threadId) == null)
            storeMap.put(threadId, new StoreImp());

        return storeMap.get(threadId);
    }


    /**
     * Deletes a Store object from the map, or does nothing if there was no object to begin with
     *
     * @param storeType Type of store to remove (local thread / global)
     */

    public static synchronized void removeStore(StoreType storeType) {
        Long threadId = (storeType == StoreType.LOCAL_THREAD) ?
                Thread.currentThread().getId() : 0;
        logger.debug("Remove store of thread (0 means global) " + threadId);
        storeMap.remove(threadId);
    }
}