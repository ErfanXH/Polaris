import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";
import UserListManager from "../managers/UserListManager";

export function useUserList() {
  const queryClient = useQueryClient();

  const {
    data: users,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ["users"],
    queryFn: async () => {
      try {
        const result = await UserListManager.getAll();
        return await UserListManager.getAll();
      } catch (err) {
        toast.error("Failed to load users");
        throw err;
      }
    },
  });

  const banMutation = useMutation({
    mutationFn: async (user) => {
      try {
        return await UserListManager.ban(user);
      } catch (err) {
        toast.error("Failed to ban user");
        throw err;
      }
    },
    onMutate: async (user) => {
      await queryClient.cancelQueries(["users"]);
      const prevUsers = queryClient.getQueryData(["users"]);

      queryClient.setQueryData(["users"], (old) =>
        old.map((u) => (u.id === user.id ? { ...u, is_banned: true } : u))
      );

      return { prevUsers };
    },
    onSuccess: () => {
      toast.success("user is banned");
    },
    onError: (_, __, context) => {
      if (context?.prevUsers) {
        queryClient.setQueryData(["users"], context.prevUsers);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries(["users"]);
    },
  });

  const allowMutation = useMutation({
    mutationFn: async (user) => {
      try {
        return await UserListManager.allow(user);
      } catch (err) {
        toast.error("Failed to unban user");
        throw err;
      }
    },
    onMutate: async (user) => {
      await queryClient.cancelQueries(["users"]);
      const prevUsers = queryClient.getQueryData(["users"]);

      queryClient.setQueryData(["users"], (old) =>
        old.map((u) => (u.id === user.id ? { ...u, is_banned: false } : u))
      );

      return { prevUsers };
    },
    onSuccess: () => {
      toast.success("user is unbanned");
    },
    onError: (_, __, context) => {
      if (context?.prevUsers) {
        queryClient.setQueryData(["users"], context.prevUsers);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries(["users"]);
    },
  });

  return {
    users,
    isLoading,
    isError,
    error,
    banUser: banMutation.mutate,
    allowUser: allowMutation.mutate,
  };
}
