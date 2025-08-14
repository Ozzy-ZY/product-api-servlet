package org.ozzy.product.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.ozzy.product.data.DataStore;
import org.ozzy.product.data.DataStoreImpl;
import org.ozzy.product.data.Product;
import java.io.*;
import java.util.Collection;
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
        if(pathInfo != null && pathInfo.length() > 1){
            int id = parseProductIdAndSetStatus(pathInfo, resp);
            if(id == -1)
                return; // status is already set
            Product product = dataStore.getProduct(id);
            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            out = resp.getWriter();
            String json = gson.toJson(product);
            out.println(json);
            out.close();
        }
        else{
            out = resp.getWriter();
            Collection<Product> resultObj = dataStore.getProducts();
            String json = gson.toJson(resultObj.toArray());
            out.println(json);
            out.close();
        }
    }

    private int parseProductIdAndSetStatus(String pathInfo, HttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            if (id <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return -1;
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
            String requestUrl = req.getRequestURL().toString();
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
            String newPath = requestUrl.substring(0, requestUrl.length() - pathInfo.length()) + "/" + product.getId();
            resp.addHeader("Location", newPath);
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