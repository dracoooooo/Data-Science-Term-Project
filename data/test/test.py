def bubble_sort(nums):
    # 每次冒泡，将最大的元素冒到最后面
    # 第一次是前n个元素，最大的元素冒到最后
    # 第二次是前n-1个元素，最大的元素冒到倒数第二个位置
    # ... ...
    n = len(nums)
    for i in range(n - 1):
        for j in range(0, n - i - 1):
            if nums[j] > nums[j + 1]:
                nums[j], nums[j + 1] = nums[j + 1], nums[j]

    return nums