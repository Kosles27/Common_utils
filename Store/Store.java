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


/**
 * Store object. <br>
 * A store object is a space where objects, values can be stored and located.
 * It follows the key value pattern. <br>
 * A store can be either Global per all threads, or local per the current thread. Hence it provides a sort of
 * global thread safe memory area. Users can either put,get or remove values from the store. Currently only one
 * implementation exists for the store, which is StoreImp
 *
 * @author <a href="mailto:nir@verisoft.co">Nir Gallner</a>
 * @see StoreImp
 * @see StoreManager
 * @see StoreType
 * @since 0.0.2 (Jan 2022)
*/


public interface Store {

    /**
     * Retrieves a value from a store. Key is given, can be any type of object. <br>
     * Recommended use for key - String value
     *
     * @param key any object which will serve as key
     * @param <T> The type of value expected to be received from the store. A template is used here
     *            for convenience, however the user is responsible to make sure the type of return value is correct
     * @return value from the store
     */
    <T> T getValueFromStore(Object key);


    /**
     * Puts a value in the store.
     *
     * @param key   Object of any type, to be served as key.
     * @param value The object to be stored
     */
    void putValueInStore(Object key, Object value);

    /**
     * Removes a value from the store
     *
     * @param key Key for the object to be removed.
     */
    void removeValueFromStore(Object key);
}