# say, these are indices (e.g. time points) of files that are supposed to be processed:
big_task = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30]

def get_one_chunk_bounds(big_task_len:int, rank:int, world_size:int):
    """
    Given a list of any-how identified individual tasks, this list is assumed
    to be 'big_task_len' items long. When this is supposed to be split into
    `world_size` chunks/sub-lists, this function returns the Python interval
    boundary (that is upper boundary is "one-beyond", just like range() is
    doing) for 'rank'-th chunk, 'rank' is zero-based and should not be equal
    or larger than 'world_size'.
    """

    chunk_size = big_task_len // world_size
    large_first_chunks_cnt = big_task_len - world_size*chunk_size
    # that many first chunks will be larger to: chunk_size+1

    idx_from = rank*chunk_size + min(rank,large_first_chunks_cnt)
    idx_next_start = (rank+1)*chunk_size + min(rank+1,large_first_chunks_cnt)

    return idx_from,idx_next_start


def demo():
    world = 4
    for rank in range(world):
        f,t = get_one_chunk_bounds(len(big_task),rank,world)
        print(f"{rank}: {f} -> {t-1} (len = {t-f})")

