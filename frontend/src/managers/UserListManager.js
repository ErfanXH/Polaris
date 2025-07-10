import { api } from "./ApiManager";

const users = [
  {
    id: 1,
    username: "ali_dev",
    email: "ali@example.com",
    phone_number: "+49123456701",
    image: "https://i.pravatar.cc/150?img=11",
    is_verified: true,
    is_banned: false,
  },
  {
    id: 2,
    username: "sara.k",
    email: "sara.k@example.com",
    phone_number: "+49123456702",
    image: "https://i.pravatar.cc/150?img=12",
    is_verified: true,
    is_banned: false,
  },
  {
    id: 3,
    username: "john_doe",
    email: "john@example.com",
    phone_number: "+49123456703",
    image: "",
    is_verified: false,
    is_banned: false,
  },
  {
    id: 4,
    username: "maria_x",
    email: "maria@example.com",
    phone_number: "+49123456704",
    image: "https://i.pravatar.cc/150?img=13",
    is_verified: true,
    is_banned: true,
  },
  {
    id: 5,
    username: "reza_123",
    email: "reza@example.com",
    phone_number: "+49123456705",
    image: "",
    is_verified: false,
    is_banned: true,
  },
  {
    id: 6,
    username: "emily_q",
    email: "emily@example.com",
    phone_number: "+49123456706",
    image: "https://i.pravatar.cc/150?img=14",
    is_verified: true,
    is_banned: false,
  },
  {
    id: 7,
    username: "hamid_b",
    email: "hamid@example.com",
    phone_number: "+49123456707",
    image: "https://i.pravatar.cc/150?img=15",
    is_verified: false,
    is_banned: false,
  },
  {
    id: 8,
    username: "linda_r",
    email: "linda@example.com",
    phone_number: "+49123456708",
    image: "https://i.pravatar.cc/150?img=16",
    is_verified: true,
    is_banned: false,
  },
  {
    id: 9,
    username: "omid_dev",
    email: "omid@example.com",
    phone_number: "+49123456709",
    image: "",
    is_verified: false,
    is_banned: true,
  },
  {
    id: 10,
    username: "niloofar_x",
    email: "niloofar@example.com",
    phone_number: "+49123456710",
    image: "https://i.pravatar.cc/150?img=17",
    is_verified: true,
    is_banned: false,
  },
];

const UserListManager = {
  getAll: async () => {
    try {
      const response = await api.get("/users/admin/all_users");
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
    // return users;
  },
  ban: async (credential) => {
    try {
      const response = await api.get("/users/admin/ban_user", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
  allow: async (credential) => {
    try {
      const response = await api.get("/users/admin/allow_user", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default UserListManager;
