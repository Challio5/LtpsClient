//package ltps1516.test;
//
//import ltps1516.gr121gr122.control.api.ApiController;
//import ltps1516.gr121gr122.model.Context;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//
///**
// * Created by rob on 16-12-15.
// */
//public class ApiControllerTest {
//
//    private final ApiController controller = new ApiController();
//
//    @BeforeClass
//    public static void init() throws Exception {
//        //Context.getInstance().setPropertyPath(ApiControllerTest.class.getResource("/config.properties").getPath());
//    }
//
//    @Before
//    public void setUp() throws Exception {
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    @Test
//    public void testGetMachineData() throws Exception {
//        // Test with valid id
//        int validMachineId = 1;
//        controller.getMachineData(validMachineId);
//
//        assertNotNull(Context.getInstance().getMachine());
//        assertEquals(Context.getInstance().getMachine().getId(), validMachineId);
//
//        // Test with invalid id
//        int invalidMachineId = 9;
//        controller.getMachineData(invalidMachineId);
//
//        assertNull(Context.getInstance().getMachine());
//    }
//
//    @Test
//    public void testLogin() throws Exception {
//        // Test with valid credentials
//        String validUsername = "admin";
//        String validPassword = "admin";
//        controller.login(validUsername, validPassword);
//
//        assertNotNull(Context.getInstance().getUser());
//        assertEquals(Context.getInstance().getUser().getUsername(), validUsername);
//        assertEquals(Context.getInstance().getUser().getPassword(), validPassword);
//
//        // Test with invalid credentials
//        controller.login("admin", "admin");
//        assertNull(Context.getInstance().getUser());
//    }
//
//    @Test
//    public void testStatusListener() throws Exception {
//        /*
//        // Test if order with status collectible exists
//        Order order = Context.getInstance().getUser().getOrders()
//                .parallelStream().filter(order1 -> order1.getStatusId() == 1).findAny().get();
//        assertNotNull(order);
//
//        // Test if collection of order
//        order.setStatusId(order.getStatusId());
//        */
//    }
//
//    @Test
//    public void testAmountListener() throws Exception {
//
//    }
//
//    @Test
//    public void testOrderListener() throws Exception {
//
//    }
//
//    @Test
//    public void testProductOrderListener() throws Exception {
//
//    }
//}