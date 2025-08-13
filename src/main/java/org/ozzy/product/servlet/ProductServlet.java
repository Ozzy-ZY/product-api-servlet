package org.ozzy.product.servlet;

import com.google.gson.Gson;
import org.ozzy.product.data.DataStore;
import org.ozzy.product.data.DataStoreImpl;
import org.ozzy.product.data.Product;
import java.io.*;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "product-servlet", value = "/product")
public class ProductServlet extends HttpServlet {
    private final DataStore dataStore = DataStoreImpl.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        addJsonHeaders(resp);
        Collection<Product> resultObj = dataStore.getProducts();
        Gson gson = new Gson();
        String json = gson.toJson(resultObj.toArray());
        PrintWriter out = resp.getWriter();
        out.println(json);
        out.flush();
        out.close();
    }
    private static void addJsonHeaders(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
}