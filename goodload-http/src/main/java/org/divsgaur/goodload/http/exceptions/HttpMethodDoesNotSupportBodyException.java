/*
Copyright (C) 2021 Goodload

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.divsgaur.goodload.http.exceptions;

import org.divsgaur.goodload.http.HttpMethod;

/**
 * Thrown when the HTTP method provided doesn't support sending a request body.
 *
 * @author Divyansh Shekhar Gaur <divyanshshekhar@users.noreply.github.com>
 * @since 1.0
 */
public class HttpMethodDoesNotSupportBodyException extends RuntimeException {
    public HttpMethodDoesNotSupportBodyException(String message) {
        super(message);
    }

    public static HttpMethodDoesNotSupportBodyException forMethod(HttpMethod httpMethod) {
        return new HttpMethodDoesNotSupportBodyException(
                String.format("%s request does not support request body", httpMethod));
    }
}
