/**
 * Copyright (C) 2010 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PircBotX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PircBotX.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircbotx;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is a small custom map that supports a Many to Many relationship, based on
 * 2 core <code>HashMap</code>'s with nested <code>HashSet</code>'s for performance.
 * <p>
 * A good example of a Many to Many relationship is this IRC bot where you have
 * users and channels. A channel has many users, and users can join many channels.
 * This can be really confusing to store and keep updated manually. This map greatly
 * simplifies this process
 *<P>
 * This class is internally synchronized and fully thread safe. 
 * @author  Leon Blakey <lord.quackstar at gmail.com>
 */
public class ManyToManyMap<A, B> {
	protected final Map<A, Set<B>> AMap = new HashMap<A, Set<B>>();
	protected final Map<B, Set<A>> BMap = new HashMap<B, Set<A>>();
	protected final Object lockObject = new Object();

	/**
	 * Gets the total number of A and B entries that this map contains
	 * @return Total size of Map
	 */
	public int size() {
		synchronized (lockObject) {
			return AMap.size() + BMap.size();
		}
	}

	/**
	 * Checks if no entries present in this Map
	 * @return True only if there are no entries present in this Map, false otherwise
	 */
	public boolean isEmpty() {
		synchronized (lockObject) {
			return AMap.isEmpty() && BMap.isEmpty();
		}
	}

	/**
	 * Resets this map to an empty state.
	 */
	public void clear() {
		synchronized (lockObject) {
			AMap.clear();
			BMap.clear();
		}
	}

	/**
	 * Checks if an A entry exists
	 * @param key The A Entry to look for
	 * @return True if entry exists, false otherwise
	 */
	public boolean containsA(A key) {
		synchronized (lockObject) {
			return AMap.containsKey(key);
		}
	}

	/**
	 * Checks if a B entry exists
	 * @param key The B Entry to look for
	 * @return True if entry exists, false otherwise
	 */
	public boolean containsB(B key) {
		synchronized (lockObject) {
			return BMap.containsKey(key);
		}
	}

	/**
	 * Gets an <b>immutable</b> view of B entries
	 * @return An <b>immutable</b> view of the B entries associated with the A entry
	 */
	public Set<A> getAValues() {
		synchronized (lockObject) {
			return Collections.unmodifiableSet(AMap.keySet());
		}
	}

	/**
	 * Gets an <b>immutable</b> view of B entries
	 * @return An <b>immutable</b> view of the B entries associated with the B entry
	 */
	public Set<B> getBValues() {
		synchronized (lockObject) {
			return Collections.unmodifiableSet(BMap.keySet());
		}
	}

	/**
	 * Gets an <b>immutable</b> view of the B entries associated with the A entry
	 * @param key The A entry to use
	 * @return An <b>immutable</b> view of the B entries associated with the A entry
	 */
	public Set<B> getBValues(A key) {
		synchronized (lockObject) {
			return Collections.unmodifiableSet(AMap.get(key));
		}
	}

	/**
	 * Gets an <b>immutable</b> view of the A entries associated with the B entry
	 * @param key The B entry to use
	 * @return An <b>immutable</b> view of the B entries associated with the B entry
	 */
	public Set<A> getAValues(B key) {
		synchronized (lockObject) {
			return Collections.unmodifiableSet(BMap.get(key));
		}
	}

	/**
	 * Adds an orphaned B entry to this map. This simply delegates to {@link #put(java.lang.Object, java.lang.Object) }
	 * @param b The B entry to store
	 * @return False as per {@link #put(java.lang.Object, java.lang.Object) }, since this adds an orphaned entry
	 */
	public boolean putA(B b) {
		return put(null, b);
	}

	/**
	 * Adds an orphaned A entry to this map. This simply delegates to {@link #put(java.lang.Object, java.lang.Object) }
	 * @param a The B entry to store
	 * @return False as per {@link #put(java.lang.Object, java.lang.Object) }, since this adds an orphaned entry
	 */
	public boolean putB(A a) {
		return put(a, null);
	}

	/**
	 * Adds a relationship to the map. If one argument is null, creates orphaned entry
	 * @param a The A entry to store
	 * @param b The B entry to store
	 * @return True if both values where associated, false if one argument is null
	 */
	public boolean put(A a, B b) {
		synchronized (lockObject) {
			//Create records if nessesary
			if (a != null && !AMap.containsKey(a))
				AMap.put(a, new HashSet<B>());
			if (b != null && !BMap.containsKey(b))
				BMap.put(b, new HashSet<A>());

			//Associate only if both A and B exist
			if (a != null && b != null) {
				AMap.get(a).add(b);
				BMap.get(b).add(a);
				return true;
			}
		}
		return false;
	}

	/**
	 * Dissociates two entries
	 * @param a
	 * @param b
	 * @return True if values where dissociated, false if not. False should only
	 *              happen if one of the entries doesn't exist
	 */
	public boolean dissociate(A a, B b) {
		return dissociate(a, b, false);
	}

	/**
	 * Dissociates two entries
	 * @param a
	 * @param b
	 * @return True if values where dissociated, false if not. False should only
	 *              happen if one of the entries doesn't exist
	 */
	public boolean dissociate(A a, B b, boolean delete) {
		synchronized (lockObject) {
			if (!AMap.containsKey(a) && !BMap.containsKey(b))
				//Nothing do dissociate
				return false;

			Set<?> values = AMap.get(a);
			values.remove(b);
			if (delete && values.isEmpty())
				deleteA(a);

			values = BMap.get(b);
			values.remove(a);
			if (delete && values.isEmpty())
				deleteB(b);
		}
		return true;
	}

	/**
	 * Deletes an A entry removing all associations
	 * @param a The A entry to delete
	 * @return The associations this entry was associated with. Null if entry does not exist
	 */
	public Set<B> deleteA(A a) {
		synchronized (lockObject) {
			//Can only remove what exists
			if (!AMap.containsKey(a))
				return null;

			//Get all associated B entries to remove later while removing A entry
			Set<B> assoications = Collections.unmodifiableSet(AMap.remove(a));

			//Clear associations in B map
			for (B curEntry : assoications)
				BMap.get(curEntry).remove(a);

			return assoications;
		}
	}

	/**
	 * Deletes a B entry removing all associations
	 * @param b The B entry to delete
	 * @return The associations this entry was associated with. Null if entry does not exist
	 */
	public Set<A> deleteB(B b) {
		synchronized (lockObject) {
			//Can only remove what exists
			if (!BMap.containsKey(b))
				return null;

			//Get all associated B entries to remove later while removing A entry
			Set<A> assoications = Collections.unmodifiableSet(BMap.remove(b));

			//Clear associations in B map
			for (A curEntry : assoications)
				AMap.get(curEntry).remove(b);

			return assoications;
		}
	}
}
