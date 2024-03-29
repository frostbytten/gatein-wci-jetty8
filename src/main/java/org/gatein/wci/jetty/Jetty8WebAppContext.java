/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.gatein.wci.jetty;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.gatein.wci.command.CommandServlet;
import org.gatein.wci.spi.WebAppContext;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.w3c.dom.Document;

public class Jetty8WebAppContext implements WebAppContext
{

   /** . */
   private Document descriptor;

   /** . */
   private ServletContext servletContext;

   /** . */
   private ClassLoader loader;

   /** . */
   private String contextPath;

   /** . */
   private final ServletContextHandler context;

   /** . */
   private ServletHolder commandServlet;

   Jetty8WebAppContext(ServletContextHandler context) throws Exception
   {
      this.context = context;
      //
      servletContext = context.getServletContext();
      loader = context.getClassLoader();
      contextPath = context.getContextPath();
   }

   public void start() throws Exception
   {
      try
      {    	  
    	  commandServlet = new ServletHolder();
    	  commandServlet.setName("JBossServlet");
    	  commandServlet.setInitOrder(0);	
    	  commandServlet.setClassName(CommandServlet.class.getName());
    	  context.addServlet(commandServlet, "/jettygateinservlet");
      }
      catch (Exception e)
      {
         cleanup();
         throw e;
      }
   }

   public void stop()
   {
      cleanup();
   }

   private void cleanup()
   {
      if (commandServlet != null)
      {
         try
         {
        	commandServlet.getServletHandler();
            commandServlet.stop();
         }
         catch (Exception e)
         {
         }
      }
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

   public ClassLoader getClassLoader()
   {
      return loader;
   }

   public String getContextPath()
   {
      return contextPath;
   }

   public boolean importFile(String parentDirRelativePath, String name, InputStream source, boolean overwrite) throws IOException
   {
      return false;
   }

   public boolean invalidateSession(String sessId)
   {
      SessionManager mgr = context.getSessionHandler().getSessionManager();
      if (mgr != null)
      {
            HttpSession sess = mgr.getHttpSession(sessId);
            if (sess != null)
            {
               sess.invalidate();
               return true;
            }
      }
      return false;
   }
}
