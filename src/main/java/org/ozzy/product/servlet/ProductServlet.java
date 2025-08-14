package org.ozzy.product.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.ozzy.product.data.DataStore;
import org.ozzy.product.data.DataStoreImpl;
import org.ozzy.product.data.Product;
import java.io.*;
import java.util.Collection;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "product-servlet", value = "/product/*")
public class ProductServlet extends HttpServlet {
    private final DataStore dataStore = DataStoreImpl.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addJsonHeaders(resp);
        Gson gson = new Gson();
        PrintWriter out;
        String pathInfo = req.getPathInfo();
        int id = 0;
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                id = Integer.parseInt(pathInfo.substring(1));
                if(id < 0)
                    throw new NumberFormatException();
            }
            catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        if (id > 0) {
            Product product = dataStore.getProduct(id);
            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            out = resp.getWriter();
            String json = gson.toJson(product);
            out.println(json);
        }
        else{
            out = resp.getWriter();
            Collection<Product> resultObj = dataStore.getProducts();
            String json = gson.toJson(resultObj.toArray());
            out.println(json);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addJsonHeaders(resp);
        Gson gson = new Gson();
        try {
            BufferedReader body = req.getReader();
            Product product = gson.fromJson(body, Product.class);
            if (!Product.isValid(product)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            dataStore.addProduct(product);
            resp.addHeader("Location", "/product/" + product.getId());
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addJsonHeaders(resp);
        Gson gson = new Gson();
        try{
            int id = Integer.parseInt(req.getParameter("id"));
            Product dataStoreProduct = dataStore.getProduct(id);
            if (dataStoreProduct == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            BufferedReader body = req.getReader();
            Product updatedProduct = gson.fromJson(body, Product.class);
            updatedProduct.setId(id);
            if (!Product.isValid(updatedProduct)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            dataStore.updateProduct(updatedProduct);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch(NumberFormatException | JsonSyntaxException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addJsonHeaders(resp);
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            dataStore.removeProduct(id);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    private static void addJsonHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}